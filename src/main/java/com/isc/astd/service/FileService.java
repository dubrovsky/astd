package com.isc.astd.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isc.astd.domain.Audit;
import com.isc.astd.domain.Doc;
import com.isc.astd.domain.File;
import com.isc.astd.domain.FilePosition;
import com.isc.astd.domain.FilePositionId;
import com.isc.astd.domain.Position;
import com.isc.astd.repository.FilePositionRepository;
import com.isc.astd.repository.FileRepository;
import com.isc.astd.service.dto.EcpDTO;
import com.isc.astd.service.dto.EcpHashDTO;
import com.isc.astd.service.dto.FileBaseDTO;
import com.isc.astd.service.dto.FileDTO;
import com.isc.astd.service.dto.FileViewDTO;
import com.isc.astd.service.dto.HashDTO;
import com.isc.astd.service.dto.PageRequestDTO;
import com.isc.astd.service.dto.PageableDTO;
import com.isc.astd.service.dto.RejectFileDTO;
import com.isc.astd.service.dto.SignedPositionDTO;
import com.isc.astd.service.mapper.Mapper;
import com.isc.astd.service.util.Utils;
import com.isc.astd.web.errors.EcpException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class FileService {

	private final FileRepository fileRepository;

	private final Mapper mapper;

	private final FileSystemService fileSystemService;

	private final DocService docService;

	private final EcpService ecpService;

	private final RouteService routeService;

	private final FilePositionRepository filePositionRepository;

	private final UserService userService;

	private final FileEcpService fileEcpService;

	private final ObjectMapper objectMapper;

	private final Utils utils;

	public FileService(FileRepository fileRepository, Mapper mapper, FileSystemService fileSystemService, @Lazy DocService docService, EcpService ecpService, RouteService routeService, FilePositionRepository filePositionRepository, UserService userService, FileEcpService fileEcpService, ObjectMapper objectMapper, Utils utils) {
		this.fileRepository = fileRepository;
		this.mapper = mapper;
		this.fileSystemService = fileSystemService;
		this.docService = docService;
		this.ecpService = ecpService;
		this.routeService = routeService;
		this.filePositionRepository = filePositionRepository;
		this.userService = userService;
		this.fileEcpService = fileEcpService;
		this.objectMapper = objectMapper;
		this.utils = utils;
	}

	@Transactional(readOnly = true)
	public PageRequestDTO<FileBaseDTO> getAllFiles(long docId, File.BranchType branchType, User user, PageableDTO pageableDTO) throws IOException {
		Page<File> files = fileRepository.findAllByDocIdAndBranchType(
				docId, branchType, PageRequest.of(pageableDTO.getPage() - 1, pageableDTO.getLimit(), utils.getSort(pageableDTO))
		);
		List<FileBaseDTO> fileDTOs = new ArrayList<>(files.getContent().size());
		Position myPosition = userService.getUser(user.getUsername()).getPosition();
		files.forEach(file -> {
			FileBaseDTO fileDTO = getFile(user, myPosition, file);
			fileDTOs.add(fileDTO);
		});
		return new PageRequestDTO<>(files.getTotalPages(), files.getTotalElements(), fileDTOs);
	}

	private FileBaseDTO getFile(User user, Position myPosition, File file) {
		FileBaseDTO fileDTO = mapper.map(file, FileBaseDTO.class);
		fileDTO.setSignedNum(fileEcpService.getSignedNum(file));
		fileDTO.setEcpPersons(fileEcpService.getEcpPersons(file));
//        fileDTO.setMoreSigns(docService.getMoreSigns(file, user));
		fileDTO.setTotalSignsNum(fileEcpService.getTotalSignsNum(file));

		SignedPositionDTO nextSignPosition = fileEcpService.getNextSignPosition(file);
		boolean canBeSigned = fileEcpService.isCanBeSigned(myPosition, file);
		boolean isNotSignedYet = fileEcpService.isNotSignedYet(user, myPosition, file, canBeSigned);
		boolean isMyOrderToSign = fileEcpService.isMyOrderToSign(myPosition, isNotSignedYet, nextSignPosition);

		fileDTO.setCanBeSigned(canBeSigned);
		fileDTO.setNotSignedYet(isNotSignedYet);
		fileDTO.setMyOrderToSign(isMyOrderToSign);
		fileDTO.setRoutePositionStatus(
				nextSignPosition != null ?
						file.getRoute().getRoutePositions().stream().
								filter(routePosition -> routePosition.getPosition().equals(nextSignPosition.getPosition()) && routePosition.getId().getOrder() == nextSignPosition.getOrder()).
								map(routePosition -> routePosition.getStatus().getText()).
								findFirst().orElse("") : ""
		);
		fileDTO.setHasPrevVersion(file.getChildFile() != null);
		if (fileDTO.isHasPrevVersion()) {
			fileDTO.setPrevVersionId(file.getChildFile().getId());
		}
		fileDTO.setHasNextVersion(file.getParentFile() != null);
		if (fileDTO.isHasNextVersion()) {
			fileDTO.setNextVersionId(file.getParentFile().getId());
		}
		return fileDTO;
	}

	public FileDTO newVersionFile(FileDTO dto, User user) throws Exception {
		File prevFile = getFile(dto.getId());
		if (prevFile.getParentFile() == null) {
			File file = mapper.map(dto, File.class);
			Doc doc = docService.getDoc(dto.getDocId());
			file.setHash(ecpService.getHash(dto.getFile().getBytes()));
			file.setRoute(routeService.getRoute(dto.getRouteId()));
			file.setStatus(File.Status.DEFAULT);
			file.setNextSignPosition(fileEcpService.getNextSignPosition(file).getPosition());
			file.setBranchType(File.BranchType.DEFAULT);
			file.setStatusModifiedBy(userService.getUser(user.getUsername()).getId());
//            file = fileRepository.save(file);
//            fileRepository.flush();
//            file.setName(generateFileName(file, doc));
			file = fileRepository.save(file);
			fileSystemService.writeFile(dto.getFile(), doc, file.getId());

			if(prevFile.getStatus() == File.Status.REJECTED){
				prevFile.setBranchType(File.BranchType.ARCHIVE);  // to hide prev file in grid
			}
			prevFile.setParentFile(file);
			fileRepository.save(prevFile);

			return mapper.map(file, FileDTO.class);
		} else {
			throw new EcpException("Уже есть новая версия листа. Индивидуальный номер: " + prevFile.getParentFile().getId());
		}
	}

	public FileDTO createFile(FileDTO dto, User user) throws Exception {
		File file = mapper.map(dto, File.class);
		file.setHash(ecpService.getHash(dto.getFile().getBytes()));
		file.setRoute(routeService.getRoute(dto.getRouteId()));
		file.setNextSignPosition(fileEcpService.getNextSignPosition(file).getPosition());
		file.setStatusModifiedBy(userService.getUser(user.getUsername()).getId());
		file.setAuditAction(Audit.Action.FILE_CREATE);
		Doc doc = docService.getDoc(dto.getDocId());
		file = fileRepository.save(file);
//        fileRepository.flush();
//        file.setName(generateFileName(file, doc));  // we need file id
//        file = fileRepository.save(file);
		if (!doc.isReadOnly()) {
			doc.setReadOnly(true);
			docService.save(doc);
		}
		fileSystemService.writeFile(dto.getFile(), doc, file.getId());
		return mapper.map(file, FileDTO.class);
	}

	public FileDTO updateFile(FileDTO dto, User user) throws Exception {
		File file = getFile(dto.getId());
		Doc doc = docService.getDoc(dto.getDocId());
		fileSystemService.deleteFile(file, doc);
		mapper.map(dto, file);
		file.setHash(ecpService.getHash(dto.getFile().getBytes()));
		file.setRoute(routeService.getRoute(dto.getRouteId()));
//        file.setNextSignPosition(fileEcpService.getNextSignPosition(file).getPosition());
		file.setStatusModifiedBy(userService.getUser(user.getUsername()).getId());
		file.setAuditAction(Audit.Action.FILE_UPDATE);
//        file.setName(generateFileName(file, doc));
		file = fileRepository.save(file);
		fileSystemService.writeFile(dto.getFile(), doc, file.getId());
		return mapper.map(file, FileDTO.class);
	}

    /*public String generateFileName(File file, Doc doc) {
        return  doc.getCatalog().getParentCatalog().getParentCatalog().getName() + "_" +
                doc.getCatalog().getParentCatalog().getName() + "_" +
                doc.getCatalog().getName() + "_" +
                doc.getNum() + "_" +
                file.getListNum() + "_" +
                file.getId() +
                ".pdf";
    }*/

	public String generateFileName(long fileId) {
		File file = getFile(fileId);
		Doc doc = file.getDoc();
		return doc.getCatalog().getParentCatalog().getParentCatalog().getName() + "_" +
				doc.getCatalog().getParentCatalog().getName() + "_" +
				doc.getCatalog().getName() + "_" +
				doc.getNum() + "_" +
				file.getListNum() + "_" +
				file.getId() + "." +
				Optional.ofNullable(file.getName())
						.filter(f -> f.contains("."))
						.map(f -> f.substring(file.getName().lastIndexOf(".") + 1)).orElse("");
	}

	public void deleteFile(long id, User user) throws IOException {
		File file = getFile(id);
		Position myPosition = userService.getUser(user.getUsername()).getPosition();
		if (file.getParentFile() == null) {
			if (myPosition.getId() == 5 && file.getStatus() == File.Status.REJECTED ||
					myPosition.getId() == 9 &&
							(file.getStatus() == File.Status.DEFAULT ||
									file.getStatus() == File.Status.SIGNING && fileEcpService.getSignedNum(file) <= 2)
			) {
				File prevVersion = file.getChildFile();
				if (prevVersion != null) {
					prevVersion.setParentFile(null);
					if(prevVersion.getStatus() == File.Status.REJECTED){
						prevVersion.setBranchType(File.BranchType.DEFAULT); // make file visible in grid
					}
					fileRepository.save(prevVersion);
					file.setChildFile(null);
				}
				file.setAuditAction(Audit.Action.FILE_DELETE);
				fileRepository.delete(file);
				fileRepository.flush();
				Doc doc = docService.getDoc(file.getDoc().getId());
				if (doc.getFiles().isEmpty()) {
					doc.setReadOnly(false);
					docService.save(doc);
				}
				fileSystemService.deleteFile(file, doc);
			} else {
				throw new EcpException("Нет прав на удаление листа");
			}
		} else {
			throw new EcpException("Удаление невозможно, есть новая версия листа. Индивидуальный номер: " + file.getParentFile().getId());
		}
	}

	public File getFile(long id) {
		return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
	}

	public FileViewDTO viewFile(long id) throws IOException {
		File file = getFile(id);
		FileViewDTO fileViewDTO = mapper.map(file, FileViewDTO.class);
		Doc doc = docService.getDoc(fileViewDTO.getDocId());
		fileViewDTO.setFile(fileSystemService.readFile(file, doc));
		return fileViewDTO;
	}

	public void archiveFile(long fileId) throws Exception {
		File oldFile = getFile(fileId);
/*
        File newFile = null;
        if(withCopy) {
            newFile = mapper.map(mapper.map(oldFile, FileDTO.class), File.class);
            newFile.setRoute(oldFile.getRoute());
            newFile.setDoc(oldFile.getDoc());
            newFile.setStatus(File.Status.DEFAULT);
            fileRepository.save(newFile);
        }
*/

        /*else {
            oldFile.setParentFile(null);
        }*/
//        oldFile.setParentFile(newFile);
		oldFile.setBranchType(File.BranchType.ARCHIVE);
		oldFile.setAuditAction(Audit.Action.FILE_ARCHIVE);
		fileRepository.save(oldFile);

		Doc doc = docService.getDoc(oldFile.getDoc().getId());
		if (!doc.isArchive()) {
			doc.setArchive(true);
			docService.save(doc);
		}

/*
        if(withCopy) {
            fileSystemService.copyFile(oldFile, newFile);
        }
*/
	}

	public void archiveCancelFile(long fileId) {
		File file = getFile(fileId);
		if (file.getParentFile() == null) {
			if (file.getStatus() == File.Status.APPROVED || file.getStatus() == File.Status.REFERENCE) {
				file.setBranchType(File.BranchType.APPROVED);
			} else {
				file.setBranchType(File.BranchType.DEFAULT);
			}

			file.setAuditAction(Audit.Action.FILE_ARCHIVE_CANCEL);
			fileRepository.save(file);

			Doc doc = docService.getDoc(file.getDoc().getId());
			if (doc.getFiles().stream().noneMatch(file1 -> file1.getBranchType() == File.BranchType.ARCHIVE)) {
				doc.setArchive(false);
				docService.save(doc);
			}
		}
	}

	public void rejectFile(long fileId, User user, RejectFileDTO rejectFile) {
		File file = getFile(fileId);
		Position position = userService.getUser(user.getUsername()).getPosition();
//        int order = getRoutePositionAnyOrder(file, position);
		SignedPositionDTO nextSignPosition = fileEcpService.getNextSignPosition(file);
		if (nextSignPosition != null && nextSignPosition.getPosition().equals(position)) {
			filePositionRepository.save(new FilePosition(new FilePositionId(file, nextSignPosition.getPosition(), nextSignPosition.getOrder()), rejectFile.getMsg()));
			file.setStatus(File.Status.REJECTED);
			file.setStatusModifiedBy(userService.getUser(user.getUsername()).getId());
//        file.setBranchType(File.BranchType.REJECTED);
			file.setAuditAction(Audit.Action.FILE_REJECT);
			fileRepository.save(file);
		}

		Doc doc = docService.getDoc(file.getDoc().getId());
		if (!doc.isRejected()) {
			doc.setRejected(true);
			docService.save(doc);
		}
	}

	public void rejectCancelFile(long fileId, User user) {
		File file = getFile(fileId);
		file.setStatus(File.Status.SIGNING);
		file.setStatusModifiedBy(userService.getUser(user.getUsername()).getId());

		FilePosition filePosition =
				file.getFilePositions().stream().
						filter(filePosition1 -> filePosition1.getEcp() == null).
						max(Comparator.comparing(filePosition1 -> filePosition1.getId().getOrder())).
						orElseThrow(() -> new RuntimeException("File Position not found"));
		file.getFilePositions().remove(filePosition);
		file.setAuditAction(Audit.Action.FILE_REJECT);
		fileRepository.save(file);

		Doc doc = docService.getDoc(file.getDoc().getId());
		if (doc.getFiles().stream().noneMatch(file1 -> file1.getStatus() == File.Status.REJECTED)) {
			doc.setRejected(false);
			docService.save(doc);
		}
	}

	@Transactional(noRollbackFor = EcpException.class)
	public EcpHashDTO getEcpData(long fileId, User user) throws Exception {
		File file = getFile(fileId);
		HashDTO hash = ecpService.getRealHash(file.getDoc(), file);
		byte[] realHash = objectMapper.writeValueAsBytes(hash);
		if (!ecpService.isRouterEcpsValid(realHash, file, userService.getUser(user.getUsername()))) {
			throw new EcpException("Предыдущая подпись не верна");
		}
		return new EcpHashDTO(user.getUsername(), hash);
	}

	@Transactional(noRollbackFor = EcpException.class)
	public void saveEcp(long fileId, User user, EcpDTO ecpDTO) throws Exception {
		byte[] ecp = ecpService.decodeEcp(ecpDTO.getEcp());

		File file = getFile(fileId);
		byte[] realHash = objectMapper.writeValueAsBytes(ecpService.getRealHash(file.getDoc(), file));
		if (!ecpService.isEcpValid(ecp, realHash)) {
			throw new EcpException("Подпись не верна");
		}

		Position position = userService.getUser(user.getUsername()).getPosition();
//        int order = getRoutePositionOrder(file, position);
		SignedPositionDTO nextSignPosition = fileEcpService.getNextSignPosition(file);
		List<File> prevFilesVersion = Collections.emptyList();
		if (nextSignPosition != null && nextSignPosition.getPosition().equals(position)) {
			FilePosition filePosition = new FilePosition(new FilePositionId(file, nextSignPosition.getPosition(), nextSignPosition.getOrder()), ecp);
//            filePositionRepository.save(filePosition);
			file.getFilePositions().add(filePosition);

			if ((fileEcpService.getSignedNum(file)) == fileEcpService.getTotalSignsNum(file)) {
				if (!ecpService.isRouterEcpsValid(realHash, file, userService.getUser(user.getUsername()))) {
					throw new EcpException("Предыдущая подпись не верна");
				}
				file.setBranchType(File.BranchType.APPROVED);
				file.setStatus(file.getRoute().getPosition().getId() == 5 ? File.Status.REFERENCE : File.Status.APPROVED);
				file.setStatusModifiedBy(userService.getUser(user.getUsername()).getId());

				prevFilesVersion = updatePrevFileVersion(file);
			} else {
				file.setStatus(File.Status.SIGNING);
				file.setStatusModifiedBy(userService.getUser(user.getUsername()).getId());
			}

			fileRepository.save(file);
		}
		nextSignPosition = fileEcpService.getNextSignPosition(file);
		file.setNextSignPosition(nextSignPosition != null ? nextSignPosition.getPosition() : null);
		file.setAuditAction(Audit.Action.FILE_SAVE_ECP);
		fileRepository.save(file);

		updatePrevSystemFilesVersion(file, prevFilesVersion);
	}

	private void updatePrevSystemFilesVersion(File file, List<File> prevFilesVersion) throws IOException {
		if(!prevFilesVersion.isEmpty()){
			for(File prevFileVersion: prevFilesVersion){
				if(prevFileVersion.getStatus() == File.Status.REJECTED && (file.getStatus() == File.Status.APPROVED || file.getStatus() == File.Status.REFERENCE)){
					fileSystemService.deleteFile(prevFileVersion, docService.getDoc(file.getDoc().getId()));
				}
			}
		}
	}

	private List<File> updatePrevFileVersion(File file) {
		List<File> prevFiles = new ArrayList<>();
		if(file.getChildFile() != null){
			File prevFileVersion = file.getChildFile();
			if(prevFileVersion.getStatus() == File.Status.REJECTED){
				while (prevFileVersion != null && prevFileVersion.getStatus() == File.Status.REJECTED){
					File prevPrevFileVersion = prevFileVersion.getChildFile();
					prevFileVersion.setParentFile(null);
					prevFileVersion.setChildFile(null);
					fileRepository.delete(prevFileVersion);
					file.setChildFile(null);
					if(prevPrevFileVersion != null) {
						prevPrevFileVersion.setParentFile(file);
						prevPrevFileVersion.setBranchType(File.BranchType.ARCHIVE);
						fileRepository.save(prevPrevFileVersion);
					}
					fileRepository.save(file);
					fileRepository.flush();
					file.setChildFile(prevPrevFileVersion);
					prevFiles.add(prevFileVersion);
					prevFileVersion = prevPrevFileVersion;
				}
			} else {
				prevFileVersion.setBranchType(File.BranchType.ARCHIVE);
				fileRepository.save(prevFileVersion);
			}
		}

		return prevFiles;
	}

	public FileBaseDTO getVersion(long fileId, String direction, User user) {
		File file = getFile(fileId);
		switch (DIRECTION.valueOf(direction.toUpperCase())) {
			case NEXT:
				file = file.getParentFile();
				break;
			case PREV:
				file = file.getChildFile();
				break;
		}
		if (file != null) {
			FileBaseDTO fileBaseDTO = mapper.map(file, FileBaseDTO.class);
			fileBaseDTO.setHasPrevVersion(file.getChildFile() != null);
			if (fileBaseDTO.isHasPrevVersion()) {
				fileBaseDTO.setPrevVersionId(file.getChildFile().getId());
			}
			fileBaseDTO.setHasNextVersion(file.getParentFile() != null);
			if (fileBaseDTO.isHasNextVersion()) {
				fileBaseDTO.setNextVersionId(file.getParentFile().getId());
			}
			fileBaseDTO.setEcpPersons(fileEcpService.getEcpPersons(file));
			return fileBaseDTO;
		}
		return new FileBaseDTO();
	}

	public FileBaseDTO getFileById(long fileId, User user) {
		File file = getFile(fileId);
		Position myPosition = userService.getUser(user.getUsername()).getPosition();
		FileBaseDTO fileDTO = getFile(user, myPosition, file);
//        fileEcpService.setMyMoreSigns(docService.getMoreSigns(file, userService.getUser(user.getUsername())), fileDTO);
		return fileDTO;
	}

	public void savePaperChange(long fileId, boolean value, String fieldName) {
		File file = getFile(fileId);
		PropertyAccessor propertyAccessor = PropertyAccessorFactory.forBeanPropertyAccess(file);
		propertyAccessor.setPropertyValue(fieldName, value);
		file.setAuditAction(Audit.Action.FILE_PAPER_COPY);
		fileRepository.save(file);
	}

	private enum DIRECTION {
		PREV, NEXT
	}
}
