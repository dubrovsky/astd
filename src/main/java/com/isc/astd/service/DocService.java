package com.isc.astd.service;

import com.isc.astd.domain.Audit;
import com.isc.astd.domain.Catalog;
import com.isc.astd.domain.Doc;
import com.isc.astd.domain.File;
import com.isc.astd.repository.DocRepository;
import com.isc.astd.repository.specification.DocSpecification;
import com.isc.astd.service.dto.*;
import com.isc.astd.service.mapper.Mapper;
import com.isc.astd.service.util.DomainUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

    private final DomainUtils domainUtils;

    private final UserService userService;

    public DocService(DocRepository docRepository, Mapper mapper, FileSystemService fileSystemService, CatalogService catalogService, FileEcpService fileEcpService, DomainUtils domainUtils, PositionService positionService, FileService fileService, UserService userService) {
        this.docRepository = docRepository;
        this.mapper = mapper;
        this.fileSystemService = fileSystemService;
        this.catalogService = catalogService;
        this.fileEcpService = fileEcpService;
        this.domainUtils = domainUtils;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public PageRequestDTO<DocDTO> getAllDocs(long catalogId, File.BranchType branchType, User user, DomainPageParamsDTO pageParams) throws IOException {
//        List<Doc> docs = docRepository.findAllByCatalogId(catalogId);
        Page<Doc> docs = docRepository.findAll(
                DocSpecification.byCatalogIdAndBranchType(catalogId, branchType),
                PageRequest.of(pageParams.getPage() - 1, pageParams.getLimit(), domainUtils.getSort(pageParams.getSort(), "npp", null))
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
                        filter(file -> file.getBranchType() == File.BranchType.DEFAULT && fileEcpService.isMyOrderToSign(file, user)).
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
    public PageRequestDTO<MoreSignsDTO> getMoreSigns(DomainPageParamsDTO pageParams, com.isc.astd.domain.User user) throws IOException {
        final List<MoreSignsDTO> docs = docRepository.findDocsWithFilesToSign(
                user.getPosition().getId(),
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null,
                pageParams.getStart(),
                pageParams.getLimit(),
                pageParams.getSort(),
//                domainUtils.getSort(pageParams.getSort(), "dateSign", Sort.Direction.ASC),
                false
        );

        final BigInteger total = docRepository.findDocsWithFilesToSign(
                user.getPosition().getId(),
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null,
                null,
                null,
                null,
                true
        );

        return new PageRequestDTO<>(total.longValue(), docs);
    }

    public PageRequestDTO<MoreSignsDTO> getMoreSignsAssure(DomainPageParamsDTO pageParams, com.isc.astd.domain.User user) throws IOException {
        final List<MoreSignsDTO> docs = docRepository.findDocsWithFilesAssureToSign(
                user.getPosition().getId(),
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null,
                pageParams.getStart(),
                pageParams.getLimit(),
                pageParams.getSort(),
                false
        );

        final BigInteger total = docRepository.findDocsWithFilesAssureToSign(
                user.getPosition().getId(),
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null,
                null,
                null,
                null,
                true
        );

        return new PageRequestDTO<>(total.longValue(), docs);
    }

    @Transactional(readOnly = true)
    public PageRequestDTO<MoreRejectedDTO> getMoreRejected(DomainPageParamsDTO pageParams, com.isc.astd.domain.User user) throws IOException {
        final List<MoreRejectedDTO> docs = docRepository.findDocsWithRejectedFiles(
                user.getPosition().getId(),
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null,
                pageParams.getStart(),
                pageParams.getLimit(),
                pageParams.getSort(),
//                domainUtils.getSort(pageParams.getSort(), "dateSign", Sort.Direction.DESC),
                false
        );

        final BigInteger total = docRepository.findDocsWithRejectedFiles(
                user.getPosition().getId(),
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null,
                null,
                null,
                null,
                true
        );

        return new PageRequestDTO<>(total.longValue(), docs);
    }

    @Transactional(readOnly = true)
    public PageRequestDTO<MoreApprovedDTO> getMoreApproved(DomainPageParamsDTO pageParams, com.isc.astd.domain.User user) throws IOException {
        final List<MoreApprovedDTO> docs = docRepository.findDocsWithApprovedFiles(
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null,
                pageParams.getStart(),
                pageParams.getLimit(),
                pageParams.getSort(),
//                domainUtils.getSort(pageParams.getSort(), "dateSign", Sort.Direction.DESC),
                false
        );

        final BigInteger total = docRepository.findDocsWithApprovedFiles(
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null,
                null,
                null,
                null,
                true
        );

        return new PageRequestDTO<>(total.longValue(), docs);
    }

    public DocDTO getDocById(long docId, User principal, File.BranchType branchType) {
        Doc doc = getDoc(docId);
        return getDoc(branchType, principal, doc);
    }

    public PageRequestDTO<DocSearchDTO> searchDocs(DomainPageParamsDTO pageParams, User principal) throws IOException {
        final com.isc.astd.domain.User user = userService.getUser(principal.getUsername());

        final List<DocSearchDTO> docs = docRepository.searchDocs(
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null,
                pageParams.getStart(),
                pageParams.getLimit(),
                pageParams.getSort(),
                pageParams.getFilter(),
                false
        );

        final BigInteger total = docRepository.searchDocs(
                user.getRootCatalog() != null ? user.getRootCatalog().getId() : null,
                null,
                null,
                null,
                pageParams.getFilter(),
                true
        );

        return new PageRequestDTO<>(total.longValue(), docs);
    }
}
