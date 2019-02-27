package com.isc.astd.config.audit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author p.dzeviarylin
 */
@Configuration
@EnableAsync
public class AuditConfig implements BeanFactoryAware {
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        AuditListener.setBeanFactory(beanFactory);
    }
}
