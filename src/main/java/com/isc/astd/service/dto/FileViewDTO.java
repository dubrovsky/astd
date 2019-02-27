package com.isc.astd.service.dto;

/**
 * @author p.dzeviarylin
 */
public class FileViewDTO extends FileBaseDTO {
    private byte[] file;

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
