package com.isc.astd.service.dto;

public class DocSearchDTO {
    private final Long docId;
    private final Long docCatalogId;
    private final Long docNpp;
    private final String docNum;
    private final String docDescr;
    private final String rootCatalogName;
    private final String docCatalogName;
    private final Long filesDefaultCount;
    private final Long filesApprovedCount;
    private final Long filesArchiveCount;
    private final Long filesAllCount;

    public DocSearchDTO(Long docId, Long docCatalogId, Long docNpp, String docNum, String docDescr, String rootCatalogName, String docCatalogName, Long filesDefaultCount, Long filesApprovedCount, Long filesArchiveCount, Long filesAllCount) {
        this.docId = docId;
        this.docCatalogId = docCatalogId;
        this.docNpp = docNpp;
        this.docNum = docNum;
        this.docDescr = docDescr;
        this.rootCatalogName = rootCatalogName;
        this.docCatalogName = docCatalogName;
        this.filesDefaultCount = filesDefaultCount;
        this.filesApprovedCount = filesApprovedCount;
        this.filesArchiveCount = filesArchiveCount;
        this.filesAllCount = filesAllCount;
    }

    public Long getDocId() {
        return docId;
    }

    public Long getDocCatalogId() {
        return docCatalogId;
    }

    public Long getDocNpp() {
        return docNpp;
    }

    public String getDocNum() {
        return docNum;
    }

    public String getDocDescr() {
        return docDescr;
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
