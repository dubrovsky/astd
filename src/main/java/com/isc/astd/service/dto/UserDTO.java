package com.isc.astd.service.dto;

/**
 * @author p.dzeviarylin
 */
public class UserDTO {

    private String id;
    private String name;
    private String positionName;
    private int positionId;
    private Long rootCatalogId;
    private String organization;

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

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String position) {
        this.positionName = position;
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
          ", positionName='" + positionName + '\'' +
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
}
