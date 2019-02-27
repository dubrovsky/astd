package com.isc.astd;

import com.isc.astd.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.annotation.PostConstruct;
import java.lang.reflect.Constructor;
import java.security.Provider;
import java.security.Security;

@SpringBootApplication
public class AstdApplication extends SpringBootServletInitializer {

    private static final Logger log = LoggerFactory.getLogger(AstdApplication.class);

    @Autowired
    private ApplicationProperties properties;

    public static void main(String[] args) {
		SpringApplication.run(AstdApplication.class, args);
	}

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AstdApplication.class);
    }

    @PostConstruct
    public void initApplication() {
        registerEcpProviders();
    }

    private void registerEcpProviders() {
        properties.getEcpProviders().forEach(provider -> {
            try {
                Class cl = Class.forName(provider);
                Constructor constructor = cl.getConstructor(new Class[]{});
                Provider p = (Provider) constructor.newInstance(new Object[]{});
                String pNm = p.getName();
                if (Security.getProvider(pNm) == null) {
                    Security.addProvider(p);
                    log.debug("add security provider: " + pNm);
                }
            } catch (Exception ex) {
                log.error("Can't init security_provider: " + provider, ex);
            }
        });
    }
}
