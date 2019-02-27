package com.isc.astd.service.dto;

/**
 * @author p.dzeviarylin
 */
public class EcpDTO {

    private String ecp;
    private boolean success = true;
    private String message;

    public EcpDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public EcpDTO() {
    }

    public String getEcp() {
        return ecp;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setEcp(String ecp) {
        this.ecp = ecp;
    }
}
