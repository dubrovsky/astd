package com.isc.astd.service.dto;

import org.apache.commons.lang3.StringUtils;

public class FilterDTO {
    private String property;
    private String value;
    private String operator;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
