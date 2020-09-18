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
    private final ImageMagick imageMagick = new ImageMagick();

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

    public ImageMagick getImageMagick() {
        return imageMagick;
    }

    public static class ImageMagick {
        private String osPath;
        private final Convert convert = new Convert();

        public Convert getConvert() {
            return convert;
        }

        public String getOsPath() {
            return osPath;
        }

        public void setOsPath(String osPath) {
            this.osPath = osPath;
        }

        public static class Convert {
            private String density;
            private String delay;
            private String loop;

            public String getDensity() {
                return density;
            }

            public void setDensity(String density) {
                this.density = density;
            }

            public String getDelay() {
                return delay;
            }

            public void setDelay(String delay) {
                this.delay = delay;
            }

            public String getLoop() {
                return loop;
            }

            public void setLoop(String loop) {
                this.loop = loop;
            }
        }
    }
}
