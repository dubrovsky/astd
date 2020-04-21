package com.isc.astd.service.dto;

import com.isc.astd.domain.File;

public class FileSearchDTO {
    private final String rootCatalogName;
    private final Long docCatalogId;
    private final String docCatalogName;
    private final Long docId;
    private final Long docNpp;
    private final String docNum;
    private final String docDescr;
    private final String descr;
    private final Long id;
    private final File.BranchType branchType;
    private final File.Status status;
    private final String listNum;

    public FileSearchDTO(String rootCatalogName, Long docCatalogId, String docCatalogName, Long docId, Long docNpp, String docNum, String docDescr, String descr, Long id, File.BranchType branchType, File.Status status, String listNum) {
        this.rootCatalogName = rootCatalogName;
        this.docCatalogId = docCatalogId;
        this.docCatalogName = docCatalogName;
        this.docId = docId;
        this.docNpp = docNpp;
        this.docNum = docNum;
        this.docDescr = docDescr;
        this.descr = descr;
        this.id = id;
        this.branchType = branchType;
        this.status = status;
        this.listNum = listNum;
    }

    public String getRootCatalogName() {
        return rootCatalogName;
    }

    public Long getDocCatalogId() {
        return docCatalogId;
    }

    public String getDocCatalogName() {
        return docCatalogName;
    }

    public Long getDocId() {
        return docId;
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

    public String getDescr() {
        return descr;
    }

    public Long getId() {
        return id;
    }

    public File.BranchType getBranchType() {
        return branchType;
    }

    public File.Status getStatus() {
        return status;
    }

    public String getStatusText() {
        return status.getText();
    }
}
