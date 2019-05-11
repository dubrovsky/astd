package com.isc.astd.service;

import com.isc.astd.domain.*;
import com.isc.astd.repository.DocRepository;
import com.isc.astd.repository.specification.DocSpecification;
import com.isc.astd.service.dto.DocDTO;
import com.isc.astd.service.dto.MoreRejectedDTO;
import com.isc.astd.service.dto.MoreSignsDTO;
import com.isc.astd.service.dto.PageRequestDTO;
import com.isc.astd.service.dto.PageableDTO;
import com.isc.astd.service.mapper.Mapper;
import com.isc.astd.service.util.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class DocService {

    private final DocRepository docRepository;

    private final Mapper mapper;

    private final FileSystemService fileSystemService;

    private final CatalogService catalogService;

    private final FileEcpService fileEcpService;

    private final Utils utils;

    private final PositionService positionService;

    private final FileService fileService;

    public DocService(DocRepository docRepository, Mapper mapper, FileSystemService fileSystemService, CatalogService catalogService, FileEcpService fileEcpService, Utils utils, PositionService positionService, FileService fileService) {
        this.docRepository = docRepository;
        this.mapper = mapper;
        this.fileSystemService = fileSystemService;
        this.catalogService = catalogService;
        this.fileEcpService = fileEcpService;
        this.utils = utils;
        this.positionService = positionService;
        this.fileService = fileService;
    }

    @Transactional(readOnly = true)
    public PageRequestDTO<DocDTO> getAllDocs(long catalogId, File.BranchType branchType, User user, PageableDTO pageableDTO) throws IOException {
//        List<Doc> docs = docRepository.findAllByCatalogId(catalogId);
        Page<Doc> docs = docRepository.findAll(
                DocSpecification.byCatalogIdAndBranchType(catalogId, branchType),
                PageRequest.of(pageableDTO.getPage() - 1, pageableDTO.getLimit(), utils.getSort(pageableDTO))
        );
        List<DocDTO> docDTOs = new ArrayList<>(docs.getContent().size());
        docs.forEach(doc -> {
            DocDTO docDTO = getDoc(branchType, user, doc);
            docDTOs.add(docDTO);
        });
        return new PageRequestDTO<>(docs.getTotalPages(), docs.getTotalElements(), docDTOs);
    }


    public DocDTO getDoc(File.BranchType branchType, User user, Doc doc) {
        DocDTO docDTO = mapper.map(doc, DocDTO.class);
        docDTO.setFilesNum(
                Math.toIntExact(doc.getFiles().stream().filter(file -> file.getBranchType() == branchType).count())
        );
        docDTO.setSignNum(
                Math.toIntExact(doc.getFiles().stream().
                        filter(file -> fileEcpService.isMyOrderToSign(file, user)).
                        count())
        );
        docDTO.setBranchType(branchType);
        return docDTO;
    }

    public DocDTO createDoc(DocDTO dto, User user) {
        Doc doc = mapper.map(dto, Doc.class);
//        Catalog rootCatalog = userService.getUser(user.getUsername()).getRootCatalog();
//        if(rootCatalog != null){
//        doc.setRootCatalog(catalogService.getRootCatalog(catalogService.getCatalog(dto.getCatalogId())));
//        }
        doc.setAuditAction(Audit.Action.DOC_CREATE);
        doc = save(doc);

        Catalog catalog = catalogService.getCatalog(dto.getCatalogId());
        if (!catalog.isReadOnly()) {
            catalog.setReadOnly(true);
            catalogService.save(catalog);
        }
        return mapper.map(doc, DocDTO.class);
    }

    public DocDTO updateDoc(long id, DocDTO dto) {
        Doc doc = getDoc(id);
        mapper.map(dto, doc);
        doc.setAuditAction(Audit.Action.DOC_UPDATE);
        doc = save(doc);
        return mapper.map(doc, DocDTO.class);
    }

    public Doc save(Doc doc) {
        return docRepository.save(doc);
    }

    public void deleteDoc(long id) throws IOException {
        Doc doc = getDoc(id);
        doc.setAuditAction(Audit.Action.DOC_DELETE);
        Catalog catalog = doc.getCatalog();
        docRepository.delete(doc);
        docRepository.flush();

        if (catalog.getDocs().isEmpty()) {
            catalog.setReadOnly(false);
            catalogService.save(catalog);
        }
        fileSystemService.deleteDoc(doc);
    }

    public Doc getDoc(long id) {
        return docRepository.findById(id).orElseThrow(() -> new RuntimeException("Doc not found"));
    }


    @Transactional(readOnly = true)
    List<MoreSignsDTO> getMoreSigns(com.isc.astd.domain.User user) {
        List<MoreSignsDTO> moreSignsDTOS = docRepository.findDocsWithFilesToSign(
                user.getPosition().getId(),
                user.getId(),
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null
        );

        final Map<Long, List<MoreSignsDTO>> docsById = moreSignsDTOS.stream().collect(Collectors.groupingBy(MoreSignsDTO::getDocId));

        List<MoreSignsDTO> result = new ArrayList<>(docsById.size());
        docsById.values().forEach(docs -> docs.forEach(moreSignsDTO -> {
            if(!result.contains(moreSignsDTO)) {
                Catalog catalog = catalogService.getCatalog(moreSignsDTO.getCatalogId());
                moreSignsDTO.setStnLine(catalog.getName());
                moreSignsDTO.setShCh(catalogService.getRootCatalog(catalog).getName());
                moreSignsDTO.setSignNum(docs.size());

                final List<FilePosition> filePositions =
                        docs.stream().
                                filter(moreSignsDTO1 ->
                                    fileEcpService.isSignedBy(positionService.getPosition(6), fileService.getFile(moreSignsDTO1.getFileId()))
                                ).
                                map(
                                    moreSignsDTO1 -> fileEcpService.getSignatureFor(positionService.getPosition(6), fileService.getFile(moreSignsDTO1.getFileId()))
                                ).
                                collect(Collectors.toList());

                moreSignsDTO.setDateShch(filePositions.stream().map(AbstractAuditingEntity::getCreatedDate).min(Instant::compareTo).orElse(null));

                result.add(moreSignsDTO);
            }
        }));

        return result;
    }

	@Transactional(readOnly = true)
	List<MoreRejectedDTO> getMoreRejected(com.isc.astd.domain.User user) {
		List<MoreRejectedDTO> moreRejectedDTOS = docRepository.findDocsWithRejectedFiles(
				user.getPosition().getId(),
				user.getId(),
				user.getRootCatalog() != null ? user.getRootCatalog().getId() : null
		);

		final Map<Long, List<MoreRejectedDTO>> docsById = moreRejectedDTOS.stream().collect(Collectors.groupingBy(MoreRejectedDTO::getDocId));

		List<MoreRejectedDTO> result = new ArrayList<>(docsById.size());
		docsById.values().forEach(docs -> docs.forEach(moreRejectedDTO -> {
			if(!result.contains(moreRejectedDTO)) {
				Catalog catalog = catalogService.getCatalog(moreRejectedDTO.getCatalogId());
				moreRejectedDTO.setStnLine(catalog.getName());
				moreRejectedDTO.setShCh(catalogService.getRootCatalog(catalog).getName());
				moreRejectedDTO.setSignNum(docs.size());

				final List<FilePosition> filePositions =
						docs.stream().
								filter(moreSignsDTO1 ->
										fileEcpService.isSignedBy(positionService.getPosition(6), fileService.getFile(moreSignsDTO1.getFileId()))
								).
								map(
										moreSignsDTO1 -> fileEcpService.getSignatureFor(positionService.getPosition(6), fileService.getFile(moreSignsDTO1.getFileId()))
								).
								collect(Collectors.toList());

				moreRejectedDTO.setDateShch(filePositions.stream().map(AbstractAuditingEntity::getCreatedDate).min(Instant::compareTo).orElse(null));

				result.add(moreRejectedDTO);
			}
		}));

		return result;
	}

    public DocDTO getDocById(long docId, User principal, File.BranchType branchType) {
        Doc doc = getDoc(docId);
	    return getDoc(branchType, principal, doc);
    }
}
