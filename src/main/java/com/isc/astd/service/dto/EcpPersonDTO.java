package com.isc.astd.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
public class EcpPersonDTO {

    private long fileId;
    private String name;

    @JsonIgnore
    private List<PositionDTO> positions;

    private String position;

    private String msg;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    private Instant createdDate;

    private Boolean invalid;

    private String routePositionStatus;

    private int routePositionOrder;

    public EcpPersonDTO(Long fileId, List<PositionDTO> positions, String position, String routePositionStatus, int routePositionOrder) {
       this.fileId = fileId;
       this.positions = positions;
       this.position = position;
       this.routePositionStatus = routePositionStatus;
       this.routePositionOrder = routePositionOrder;
    }

    public EcpPersonDTO() {
    }

    public String getName() {
        return name;
    }

    public List<PositionDTO> getPositions() {
        return positions;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(Boolean invalid) {
        this.invalid = invalid;
    }

    public long getFileId() {
        return fileId;
    }

    public String getRoutePositionStatus() {
        return routePositionStatus;
    }

    public void setRoutePositionStatus(String routePositionStatus) {
        this.routePositionStatus = routePositionStatus;
    }

    public int getRoutePositionOrder() {
        return routePositionOrder;
    }

    public void setRoutePositionOrder(int routePositionOrder) {
        this.routePositionOrder = routePositionOrder;
    }
}
