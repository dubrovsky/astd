package com.isc.astd.domain.converter;

import com.isc.astd.domain.Catalog;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author p.dzeviarylin
 */
@Converter(autoApply = true)
public class CatalogTypeConverter implements AttributeConverter<Catalog.Type, String> {
    @Override
    public String convertToDatabaseColumn(Catalog.Type catalogType) {
        return catalogType.getCode();
    }

    @Override
    public Catalog.Type convertToEntityAttribute(String dbData) {
        for (Catalog.Type catalogType : Catalog.Type.values()) {
            if (catalogType.getCode().equals(dbData)) {
                return catalogType;
            }
        }

        throw new IllegalArgumentException("Unknown database value:" + dbData);
    }
}
