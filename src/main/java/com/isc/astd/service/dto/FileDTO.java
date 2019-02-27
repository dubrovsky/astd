package com.isc.astd.service.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author p.dzeviarylin
 */
public class FileDTO extends FileBaseDTO {

    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
