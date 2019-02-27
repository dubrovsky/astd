package com.isc.astd.domain.converter;

import com.isc.astd.domain.RoutePosition;

import javax.persistence.AttributeConverter;

/**
 * @author p.dzeviarylin
 */
public class RoutePositionStatusConverter implements AttributeConverter<RoutePosition.Status, String> {
    @Override
    public String convertToDatabaseColumn(RoutePosition.Status attribute) {
        return attribute.getCode();
    }

    @Override
    public RoutePosition.Status convertToEntityAttribute(String dbData) {
        for (RoutePosition.Status status : RoutePosition.Status.values()) {
            if (status.getCode().equals(dbData)) {
                return status;
            }
        }

        throw new IllegalArgumentException("Unknown database value:" + dbData);
    }
}
