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
        if (StringUtils.isNotBlank(value)) {
            StringBuilder sb = new StringBuilder("'");
            if (operator.equals("like")) {
                sb.append("%").append(value).append("%");
            } else {
                sb.append(value);
            }
            sb.append("'");
            return sb.toString();
        } else {
            return null;
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperator() {
        if (operator.equals("like")) {
            return " LIKE ";
        }
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
