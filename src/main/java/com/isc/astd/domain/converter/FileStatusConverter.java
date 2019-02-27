package com.isc.astd.domain.converter;

import com.isc.astd.domain.File;

import javax.persistence.AttributeConverter;

/**
 * @author p.dzeviarylin
 */
public class FileStatusConverter implements AttributeConverter<File.Status, String> {
    @Override
    public String convertToDatabaseColumn(File.Status attribute) {
        return attribute.getCode();
    }

    @Override
    public File.Status convertToEntityAttribute(String dbData) {
        for (File.Status status : File.Status.values()) {
            if (status.getCode().equals(dbData)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown database value:" + dbData);
    }
}
