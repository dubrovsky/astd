package com.isc.astd.service;

import by.avest.crypto.pkcs.pkcs7.PKCS7;
import com.isc.astd.config.ApplicationProperties;
import com.isc.astd.domain.*;
import com.isc.astd.repository.FilePositionRepository;
import com.isc.astd.repository.FileRepository;
import com.isc.astd.repository.FileReviewRepository;
import com.isc.astd.service.dto.HashDTO;
import com.isc.astd.service.util.AvDirPKIXBuilderParameters;
import com.isc.astd.service.util.EcpUtils;
import com.isc.astd.service.util.Pkcs7VerifyAv;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.security.cert.PKIXBuilderParameters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author p.dzeviarylin
 */
@Service
public class EcpService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ApplicationProperties properties;

    private final FileSystemService fileSystemService;

    private final FilePositionRepository filePositionRepository;

    private final FileReviewRepository fileReviewRepository;

    private final FileRepository fileRepository;

    private final PKIXBuilderParameters pkixBuilderParameters;

    public EcpService(ApplicationProperties properties, FileSystemService fileSystemService, FilePositionRepository filePositionRepository, FileReviewRepository fileReviewRepository, FileRepository fileRepository) throws Exception {
        this.properties = properties;
        pkixBuilderParameters = new AvDirPKIXBuilderParameters(Paths.get(properties.getStoragePath()).toString()).get();
        this.fileSystemService = fileSystemService;
        this.filePositionRepository = filePositionRepository;
        this.fileReviewRepository = fileReviewRepository;
        this.fileRepository = fileRepository;
    }

    boolean isEcpValid(byte[] ecp, byte[] hash) throws Exception {
        PKCS7 pkcs72 = new PKCS7(ecp);
        Pkcs7VerifyAv pkcs7VerifyAv = new Pkcs7VerifyAv(pkixBuilderParameters, pkcs72, hash);
        boolean ret = pkcs7VerifyAv.verify();
        log.debug(pkcs7VerifyAv.toString());
        return ret;
    }

    public String getHash(byte[] file) throws Exception {
        return EcpUtils.getHash(file, properties.getEcpAlgorithm());
    }

    boolean isHashValid(byte[] dbHash, byte[] realHash) {
        return Arrays.equals(dbHash, realHash);
    }

    List<HashDTO> getDbHash(Doc doc) {
        List<HashDTO> hashs = new ArrayList<>(doc.getFiles().size());
        List<File> toSort = sortFiles(doc);
        for (File file : toSort) {
            hashs.add(new HashDTO(file.getId(), file.getHash()));
        }
        return hashs;
    }

    List<HashDTO> getRealHash(Doc doc) throws Exception {
        List<HashDTO> hashs = new ArrayList<>(doc.getFiles().size());
        List<File> toSort = sortFiles(doc);
        for (File file : toSort) {
            byte[] bytes = fileSystemService.readFile(file);
            hashs.add(new HashDTO(file.getId(), getHash(bytes)));
        }
        return hashs;
    }

    public HashDTO getRealHash(long fileId) throws Exception {
        File file = fileRepository.getOne(fileId);
        byte[] bytes = fileSystemService.readFile(file);
        return new HashDTO(fileId, getHash(bytes));
    }

    private List<File> sortFiles(Doc doc) {
        List<File> toSort = new ArrayList<>(doc.getFiles());
        toSort.sort(Comparator.comparing(AbstractBaseEntity::getId));
        return toSort;
    }

    byte[] decodeEcp(String ecp) {
        byte[] data;
        if (Base64.isBase64(ecp)) {
            data = Base64.decodeBase64(ecp);
        } else {
            data = ecp.getBytes();
        }
        return data;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean fileCheckAllEcpsAndUpdate(byte[] hash, long fileId, String userName) throws Exception {
        boolean result = true;
        final File file = fileRepository.getOne(fileId);  // to make Propagation.REQUIRES_NEW work
        final List<FilePosition> filePositions = file.getFilePositions().stream().filter(filePosition -> filePosition.getEcp() != null).collect(Collectors.toList());
        for (FilePosition filePosition : filePositions) {
            if (!isEcpValid(filePosition.getEcp(), hash)) { // invalid ecp
                if (!filePosition.isInvalid()) {
                    filePosition.setInvalid(true);
                    filePositionRepository.save(filePosition);
                }
            } else if (filePosition.isInvalid()) {
                filePosition.setInvalid(false);
                filePositionRepository.save(filePosition);
            }
        }
        if (filePositions.stream().anyMatch(FilePosition::isInvalid)) {
            if (file.getStatus() != File.Status.INVALID) {
                file.setStatusPrev(file.getStatus());
                file.setStatus(File.Status.INVALID);
                file.setStatusModifiedBy(userName);
                fileRepository.save(file);
            }
            result = false;
        } else if (file.getStatus() == File.Status.INVALID) {
            file.setStatus(file.getStatusPrev());
            file.setStatusPrev(null);
            file.setStatusModifiedBy(userName);
            fileRepository.save(file);
        }
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean fileReviewCheckLastEcpAndUpdate(byte[] hash, long fileId) throws Exception {
        boolean result = true;
        final File file = fileRepository.getOne(fileId);  // to make Propagation.REQUIRES_NEW work
        final FileReview fileReview =
                file.getFileReviews().stream()
//                        .filter(fileReview_ -> fileReview_.getEcp() != null)
                        .max(Comparator.comparing(AbstractAuditingEntity::getCreatedDate))
                        .orElse(null);

        if (fileReview != null) {
            if (!isEcpValid(fileReview.getEcp(), hash)) { // invalid ecp
                if (!fileReview.isInvalid()) {
                    fileReview.setInvalid(true);
                    fileReviewRepository.save(fileReview);
                    file.setStatusReview(File.StatusReview.INVALID);
                    fileRepository.save(file);
                }
                result = false;
            } else if (fileReview.isInvalid()) {
                fileReview.setInvalid(false);
                fileReviewRepository.save(fileReview);
                file.setStatusReview(File.StatusReview.SIGNED);
                fileRepository.save(file);
            }
        }
        return result;

    }
}
