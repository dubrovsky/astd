package com.isc.astd.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.x509.X509PrincipalExtractor;
import org.springframework.stereotype.Component;

import java.security.cert.X509Certificate;

/**
 * @author p.dzeviarylin
 */
@Component
public class X509PrincipalSnExtractor implements X509PrincipalExtractor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object extractPrincipal(X509Certificate clientCert) {

        if (clientCert.getSerialNumber() == null) {
            throw new BadCredentialsException("Не найден серийный номер");
        }

        String serialNumber = clientCert.getSerialNumber().toString(16);

        log.debug(String.valueOf(clientCert.getSubjectDN()));
        log.debug("serialNumber: " + serialNumber);

        return serialNumber;
    }
}
