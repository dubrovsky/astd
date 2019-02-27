package com.isc.astd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private String basePath;
    private String storagePath;
    private List<String> ecpProviders;
    private String ecpAlgorithm;

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public List<String> getEcpProviders() {
        return ecpProviders;
    }

    public void setEcpProviders(List<String> ecpProviders) {
        this.ecpProviders = ecpProviders;
    }

    public String getEcpAlgorithm() {
        return ecpAlgorithm;
    }

    public void setEcpAlgorithm(String ecpAlgorithm) {
        this.ecpAlgorithm = ecpAlgorithm;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
}
