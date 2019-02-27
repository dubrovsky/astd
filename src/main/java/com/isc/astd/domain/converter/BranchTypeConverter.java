package com.isc.astd.domain.converter;

import com.isc.astd.domain.File;

import javax.persistence.AttributeConverter;

/**
 * @author p.dzeviarylin
 */
public class BranchTypeConverter implements AttributeConverter<File.BranchType, String> {
    @Override
    public String convertToDatabaseColumn(File.BranchType attribute) {
        return attribute.getCode();
    }

    @Override
    public File.BranchType convertToEntityAttribute(String dbData) {
        for (File.BranchType branchType : File.BranchType.values()) {
            if (branchType.getCode().equals(dbData)) {
                return branchType;
            }
        }

        throw new IllegalArgumentException("Unknown database value:" + dbData);
    }
}

