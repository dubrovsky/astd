package com.isc.astd.domain.converter;

import com.isc.astd.domain.File;

import javax.persistence.AttributeConverter;

public class FileReviewStatusConverter implements AttributeConverter<File.StatusReview, String> {
    @Override
    public String convertToDatabaseColumn(File.StatusReview attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public File.StatusReview convertToEntityAttribute(String dbData) {
        if (dbData != null) {
            for (File.StatusReview status : File.StatusReview.values()) {
                if (status.getCode().equals(dbData)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown database value:" + dbData);
        }

        return null;
    }
}