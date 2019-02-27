package com.isc.astd.domain.converter;

import com.isc.astd.domain.Audit;

import javax.persistence.AttributeConverter;

/**
 * @author p.dzeviarylin
 */
public class EntityAuditActionConverter implements AttributeConverter<Audit.Action, String> {
    @Override
    public String convertToDatabaseColumn(Audit.Action attribute) {
        return attribute.getCode();
    }

    @Override
    public Audit.Action convertToEntityAttribute(String dbData) {
        for (Audit.Action action : Audit.Action.values()) {
            if (action.getCode().equals(dbData)) {
                return action;
            }
        }

        throw new IllegalArgumentException("Unknown database value:" + dbData);
    }
}
