package com.isc.astd.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

/**
 * @author p.dzeviarylin
 */
public class EcpReviewPersonDTO {

    private final long fileId;
    private final String name;
    private final String position;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "GMT+3")
    private final Instant createdDate;
    private final Boolean invalid;


    public EcpReviewPersonDTO(long fileId, String name, String position, Instant createdDate, Boolean invalid) {
        this.fileId = fileId;
        this.name = name;
        this.position = position;
        this.createdDate = createdDate;
        this.invalid = invalid;
    }

    public long getFileId() {
        return fileId;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Boolean getInvalid() {
        return invalid;
    }
}
