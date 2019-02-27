package com.isc.astd.service.dto;

/**
 * @author p.dzeviarylin
 */
public class EcpHashDTO {

    private final String serial;
//    private final List<HashDTO> signData;
    private final HashDTO signData;

    public EcpHashDTO(String serial, HashDTO signData) {
        this.serial = serial;
        this.signData = signData;
    }

    public String getSerial() {
        return serial;
    }

    public HashDTO getSignData() {
        return signData;
    }

//    public List<HashDTO> getSignData() {
//        return signData;
//    }
}
