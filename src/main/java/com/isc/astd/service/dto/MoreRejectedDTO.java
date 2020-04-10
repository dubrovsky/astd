package com.isc.astd.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

/**
 * @author p.dzeviarylin
 */
public class MoreRejectedDTO {

	private final Long docId;
	private final long catalogId;
	private final long fileId;
	private final long npp;
	private final String descr;
	private final String noteShl;
	private final long listCount;
	private final String shCh;
	private final String stnLine;
	private final String num;
	private final String msg;
	@JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
	private final Instant dateSign;

	public MoreRejectedDTO(String shCh, long catalogId, String stnLine, Long docId, long npp, String num, String descr, long fileId, Instant dateSign, long listCount, String noteShl, String msg) {
		this.docId = docId;
		this.catalogId = catalogId;
		this.fileId = fileId;
		this.npp = npp;
		this.descr = descr;
		this.noteShl = noteShl;
		this.listCount = listCount;
		this.shCh = shCh;
		this.stnLine = stnLine;
		this.num = num;
		this.msg = msg;
		this.dateSign = dateSign;
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

	public long getListCount() {
		return listCount;
	}

	public String getNum() {
		return num;
	}

	public String getShCh() {
		return shCh;
	}

	public String getStnLine() {
		return stnLine;
	}

	public Instant getDateSign() {
		return dateSign;
	}

	public long getFileId() {
		return fileId;
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

	public String getMsg() {
		return msg;
	}
}
