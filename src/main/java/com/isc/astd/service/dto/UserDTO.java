package com.isc.astd.service.dto;

/**
 * @author p.dzeviarylin
 */
public class UserDTO extends UserBaseDTO {

	private String positionName;
	private String rootCatalogName;

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}

	public String getRootCatalogName() {
		return rootCatalogName;
	}

	public void setRootCatalogName(String rootCatalogName) {
		this.rootCatalogName = rootCatalogName;
	}

	@Override
	public String toString() {
		return "UserDTO{" +
				"id='" + getId() + '\'' +
				", name='" + getName() + '\'' +
				", positionName='" + positionName + '\'' +
				", organization='" + getOrganization() + '\'' +
				'}';
	}
}
