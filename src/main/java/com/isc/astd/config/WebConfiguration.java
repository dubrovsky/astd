package com.isc.astd.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author p.dzeviarylin
 */
@Configuration
public class WebConfiguration implements ServletContextInitializer {

    private final Logger log = LoggerFactory.getLogger(WebConfiguration.class);

    private final Environment env;

    public WebConfiguration(Environment env) {
        this.env = env;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

    }
}
