package com.isc.astd.service.dto;

/**
 * @author p.dzeviarylin
 */
public class HashDTO {

    private final long id;
    private final String hash;

    public HashDTO(long id, String hash) {
        this.id = id;
        this.hash = hash;
    }

    public long getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }
}
