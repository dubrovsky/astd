package com.isc.astd.config.audit;

import com.isc.astd.service.AuditService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

/**
 * @author p.dzeviarylin
 */
public class AuditListener extends AuditingEntityListener {

    private static BeanFactory beanFactory;

    @PostPersist
    public void onPostCreate(Object target) {
        AuditService entityAuditService = beanFactory.getBean(AuditService.class);
        entityAuditService.saveAuditEvent(target);
    }

    @PostUpdate
    public void onPostUpdate(Object target) {
        AuditService entityAuditService = beanFactory.getBean(AuditService.class);
        entityAuditService.saveAuditEvent(target);
    }

    @PostRemove
    public void onPostRemove(Object target) {
        AuditService entityAuditService = beanFactory.getBean(AuditService.class);
        entityAuditService.saveAuditEvent(target);
    }

    static void setBeanFactory(BeanFactory beanFactory) {
        AuditListener.beanFactory = beanFactory;
    }
}
