package com.isc.astd.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

/**
 * @author p.dzeviarylin
 */
public class MoreSignsDTO {

    private final Long docId;
    private final long catalogId;
    private final long fileId;
    private final long npp;
    private final String descr;
    private final String noteShl;
    private long signNum;
    private String shCh;
    private String stnLine;
    private final String num;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    private Instant dateShch;

    public MoreSignsDTO(long docId, long catalogId, long npp, String num, String descr,/* long signNum,*/ long fileId, String noteShl) {
        this.docId = docId;
        this.catalogId = catalogId;
        this.npp = npp;
        this.num = num;
        this.descr = descr;
//        this.signNum = signNum;
        this.fileId = fileId;
	    this.noteShl = noteShl;
    }

    public Long getDocId() {
        return docId;
    }

    public long getCatalogId() {
        return catalogId;
    }

    public long getNpp() {
        return npp;
    }

    public String getDescr() {
        return descr;
    }

    public long getSignNum() {
        return signNum;
    }

    public String getNum() {
        return num;
    }

    public String getShCh() {
        return shCh;
    }

    public void setShCh(String shCh) {
        this.shCh = shCh;
    }

    public String getStnLine() {
        return stnLine;
    }

    public void setStnLine(String stnLine) {
        this.stnLine = stnLine;
    }

    public Instant getDateShch() {
        return dateShch;
    }

    public void setDateShch(Instant date) {
        this.dateShch = date;
    }

    public long getFileId() {
        return fileId;
    }

    public void setSignNum(long signNum) {
        this.signNum = signNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoreSignsDTO)) return false;
        return getDocId() != null && getDocId().equals(((MoreSignsDTO) o).getDocId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

	public String getNoteShl() {
		return noteShl;
	}
}
