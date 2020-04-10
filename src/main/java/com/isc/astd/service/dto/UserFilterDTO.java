package com.isc.astd.service.dto;

/**
 * @author p.dzeviarylin
 */
public class UserFilterDTO {

    private Long catalogId;
    private Long positionId;

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }
}
