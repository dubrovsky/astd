package com.isc.astd.service.dto;

import com.isc.astd.domain.File;

/**
 * @author p.dzeviarylin
 */
public class DocDTO {

    private Long id;
    private Long catalogId;
    private Long npp;
    private String num;
    private String descr;
    private String noteShl;
    private int filesNum = 0;
    private int signNum = 0;
    private File.BranchType branchType = File.BranchType.DEFAULT;
    private boolean readOnly;
    private boolean archive;
    private boolean rejected;
    private Long rootCatalogId;

    public DocDTO() {
    }

    public DocDTO(Long id, Long catalogId, Long npp, String descr, int signNum) {
        this.id = id;
        this.catalogId = catalogId;
        this.npp = npp;
        this.descr = descr;
        this.signNum = signNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public Long getNpp() {
        return npp;
    }

    public void setNpp(Long npp) {
        this.npp = npp;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getFilesNum() {
        return filesNum;
    }

    public void setFilesNum(int filesNum) {
        this.filesNum = filesNum;
    }

    public File.BranchType getBranchType() {
        return branchType;
    }

    public void setBranchType(File.BranchType branchType) {
        this.branchType = branchType;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public int getSignNum() {
        return signNum;
    }

    public void setSignNum(int signNum) {
        this.signNum = signNum;
    }

    public Long getRootCatalogId() {
        return rootCatalogId;
    }

    public void setRootCatalogId(Long rootCatalogId) {
        this.rootCatalogId = rootCatalogId;
    }

    public String getNoteShl() {
        return noteShl;
    }

    public void setNoteShl(String noteShl) {
        this.noteShl = noteShl;
    }
}
