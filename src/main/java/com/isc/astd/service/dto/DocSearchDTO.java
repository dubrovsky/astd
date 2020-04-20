package com.isc.astd.service.dto;

public class DocSearchDTO {
    private final Long id;
    private final Long npp;
    private final String num;
    private final String descr;
    private final Long docCatalogId;
    private final String rootCatalogName;
    private final String docCatalogName;
    private final Long filesDefaultCount;
    private final Long filesApprovedCount;
    private final Long filesArchiveCount;
    private final Long filesAllCount;

    public DocSearchDTO(Long id, Long docCatalogId, Long npp, String num, String descr, String rootCatalogName, String docCatalogName, Long filesDefaultCount, Long filesApprovedCount, Long filesArchiveCount, Long filesAllCount) {
        this.id = id;
        this.docCatalogId = docCatalogId;
        this.npp = npp;
        this.num = num;
        this.descr = descr;
        this.rootCatalogName = rootCatalogName;
        this.docCatalogName = docCatalogName;
        this.filesDefaultCount = filesDefaultCount;
        this.filesApprovedCount = filesApprovedCount;
        this.filesArchiveCount = filesArchiveCount;
        this.filesAllCount = filesAllCount;
    }

    public Long getId() {
        return id;
    }

    public Long getDocCatalogId() {
        return docCatalogId;
    }

    public Long getNpp() {
        return npp;
    }

    public String getNum() {
        return num;
    }

    public String getDescr() {
        return descr;
    }

    public String getRootCatalogName() {
        return rootCatalogName;
    }

    public String getDocCatalogName() {
        return docCatalogName;
    }

    public Long getFilesDefaultCount() {
        return filesDefaultCount;
    }

    public Long getFilesApprovedCount() {
        return filesApprovedCount;
    }

    public Long getFilesArchiveCount() {
        return filesArchiveCount;
    }

    public Long getFilesAllCount() {
        return filesAllCount;
    }
}
