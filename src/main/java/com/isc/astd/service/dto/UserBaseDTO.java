package com.isc.astd.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * @author p.dzeviarylin
 */
public class UserBaseDTO {

	private String id;
	private String prevId;
	private String name;
	private int positionId;
	private Long rootCatalogId;
	private String organization;
	@JsonFormat(pattern = "dd.MM.yyyy", timezone = "GMT+3")
	private LocalDate expiredDate;
    private String phone;
    private String email;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Override
	public String toString() {
		return "UserDTO{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", organization='" + organization + '\'' +
				'}';
	}

	public int getPositionId() {
		return positionId;
	}

	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}

	public Long getRootCatalogId() {
		return rootCatalogId;
	}

	public void setRootCatalogId(Long rootCatalogId) {
		this.rootCatalogId = rootCatalogId;
	}

	public LocalDate getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(LocalDate expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getPrevId() {
		return prevId;
	}

	public void setPrevId(String prevId) {
		this.prevId = prevId;
	}

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
