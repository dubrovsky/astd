package com.isc.astd.security;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.security.cert.X509Certificate;

/**
 * @author p.dzeviarylin
 */
public class X509WebAuthenticationDetails extends WebAuthenticationDetails {
    
    private final X509Certificate x509Certificate;

    public X509WebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.x509Certificate = extractClientCertificate(request);
    }

    private X509Certificate extractClientCertificate(HttpServletRequest request) {
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        if (certs != null && certs.length > 0) {
            return certs[0];
        }
        return null;
    }

    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }
}
