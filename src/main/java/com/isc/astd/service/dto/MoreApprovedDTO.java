package com.isc.astd.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

/**
 * @author p.dzeviarylin
 */
public class MoreApprovedDTO {

    private final Long docId;
    private final Long catalogId;
    private final Long fileId;

    private final Long npp;
    private final String descr;
    private final String shCh;
    private final String stnLine;
    private final String num;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    private final Instant dateSign;
    private final Long listCount;
    private final boolean paperShL;
    private final boolean paperShChTD;

    /*public MoreApprovedDTO(long docId, long catalogId, long npp, String num, String descr, long fileId) {
        this.docId = docId;
        this.catalogId = catalogId;
        this.npp = npp;
        this.num = num;
        this.descr = descr;
        this.fileId = fileId;
    }*/

    public MoreApprovedDTO(String shCh, Long catalogId, String stnLine, Long docId, Long npp, String num, String descr, Long fileId, Instant dateSign, Long listCount, boolean paperShL, boolean paperShChTD) {
        this.shCh = shCh;
        this.catalogId = catalogId;
        this.stnLine = stnLine;
        this.docId = docId;
        this.npp = npp;
        this.num = num;
        this.descr = descr;
        this.fileId = fileId;
        this.dateSign = dateSign;
        this.listCount = listCount;
        this.paperShL = paperShL;
        this.paperShChTD = paperShChTD;
    }

    public Long getDocId() {
        return docId;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public Long getFileId() {
        return fileId;
    }

    public Long getNpp() {
        return npp;
    }

    public String getDescr() {
        return descr;
    }

    public String getShCh() {
        return shCh;
    }

//    public void setShCh(String shCh) {
//        this.shCh = shCh;
//    }

    public String getStnLine() {
        return stnLine;
    }

//    public void setStnLine(String stnLine) {
//        this.stnLine = stnLine;
//    }

    public String getNum() {
        return num;
    }

    public Instant getDateSign() {
        return dateSign;
    }

//    public void setDateSign(Instant dateSign) {
//        this.dateSign = dateSign;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoreApprovedDTO)) return false;
        return getDocId() != null && getDocId().equals(((MoreApprovedDTO) o).getDocId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public Long getListCount() {
        return listCount;
    }

    public boolean isPaperShL() {
        return paperShL;
    }

    public boolean isPaperShChTD() {
        return paperShChTD;
    }

//    public void setListCount(long listCount) {
//        this.listCount = listCount;
//    }
}
