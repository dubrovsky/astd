package com.isc.astd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

/**
 * @author p.dzeviarylin
 */
@Configuration
public class DateTimeConfiguration implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
		registrar.setDateFormatter(DateTimeFormatter.ofPattern("dd.MM.yy"));
		registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss"));
		registrar.registerFormatters(registry);
	}

}
