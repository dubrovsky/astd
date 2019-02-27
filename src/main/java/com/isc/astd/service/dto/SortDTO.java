package com.isc.astd.service.dto;

/**
 * @author p.dzeviarylin
 */
public class SortDTO {
    private String property;
    private String direction;

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
