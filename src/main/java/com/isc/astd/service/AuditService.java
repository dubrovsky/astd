package com.isc.astd.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isc.astd.domain.AbstractAuditingEntity;
import com.isc.astd.domain.Audit;
import com.isc.astd.repository.AuditRepository;
import com.isc.astd.service.dto.AuditDTO;
import com.isc.astd.service.dto.PageRequestDTO;
import com.isc.astd.service.dto.PageableDTO;
import com.isc.astd.service.json.Views;
import com.isc.astd.service.mapper.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AuditService {

    private final AuditRepository entityAuditRepository;

    private final ObjectMapper objectMapper;

    private final Mapper mapper;

    private final UserService userService;

    public AuditService(AuditRepository auditingEntityRepository, ObjectMapper objectMapper, Mapper mapper, UserService userService) {
        this.entityAuditRepository = auditingEntityRepository;
        this.objectMapper = objectMapper;
        this.mapper = mapper;
        this.userService = userService;
    }

    @Async
    public void saveAuditEvent(Object target) {
        Audit auditedEntity = prepareAuditEntity(target);
        if (auditedEntity != null) {
            entityAuditRepository.save(auditedEntity);
        }
    }

    private Audit prepareAuditEntity(Object entity) {
        if(!(entity instanceof AbstractAuditingEntity)) {
            return null;
        }
        AbstractAuditingEntity auditingEntity = (AbstractAuditingEntity) entity;
        if(auditingEntity.getAuditAction() == null){
            return null;
        }
        
        Audit auditedEntity = new Audit();
        Class<?> entityClass = entity.getClass(); // Retrieve entity class with reflection

        auditedEntity.setEntityType(entityClass.getSimpleName());
        String entityId;
        String entityData;
        try {
            Field privateLongField = getField(entityClass,"id");
            privateLongField.setAccessible(true);
            entityId = String.valueOf(privateLongField.get(entity));
            privateLongField.setAccessible(false);
            entityData = objectMapper.writerWithDefaultPrettyPrinter().withView(Views.Audit.class).writeValueAsString(entity);
        } catch (IllegalArgumentException | IllegalAccessException | SecurityException |
                IOException e) {
            return null;
        }
//        auditedEntity.setAction(action);
        auditedEntity.setAction(auditingEntity.getAuditAction());
        auditedEntity.setEntityId(entityId);
        auditedEntity.setEntityValue(entityData);
        final AbstractAuditingEntity abstractAuditEntity = (AbstractAuditingEntity) entity;
//        if (EntityAudit.Action.CREATE == action) {
        if (auditingEntity.getAuditAction().getCode().contains("create")) {
            auditedEntity.setCreatedBy(abstractAuditEntity.getCreatedBy());
            auditedEntity.setCreatedDate(abstractAuditEntity.getCreatedDate());
            auditedEntity.setCommitVersion(1);
        } else {
            auditedEntity.setCreatedBy(abstractAuditEntity.getLastModifiedBy());
            auditedEntity.setCreatedDate(abstractAuditEntity.getLastModifiedDate());
            calculateVersion(auditedEntity);
        }
        return auditedEntity;
    }

    private void calculateVersion(Audit auditedEntity) {
        Integer lastCommitVersion = entityAuditRepository.findMaxCommitVersion(auditedEntity.getEntityType(), auditedEntity.getEntityId());
        if(lastCommitVersion != null && lastCommitVersion != 0){
            auditedEntity.setCommitVersion(lastCommitVersion + 1);
        } else {
            auditedEntity.setCommitVersion(1);
        }
    }

    public static Field getField(Class<?> clazz, String name) {
        Field field = null;
        while (clazz != null && field == null) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (Exception ignored) {
            }
            clazz = clazz.getSuperclass();
        }
        return field;
    }

    public static void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = getField(object.getClass(), fieldName);
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    public PageRequestDTO<AuditDTO> getAllEntities(User user, PageableDTO pageableDTO) {
        Page<Audit> entityAudits = entityAuditRepository.findAll(
                PageRequest.of(pageableDTO.getPage() - 1, pageableDTO.getLimit(), Sort.by(Sort.Direction.DESC, "id"))
        );
        List<AuditDTO> auditDTOs = new ArrayList<>(entityAudits.getContent().size());
        entityAudits.forEach(entityAudit -> {
            AuditDTO entityAuditDTO = mapper.map(entityAudit, AuditDTO.class);
            entityAuditDTO.setAction(entityAudit.getAction().getText());
            final com.isc.astd.domain.User user_ = userService.getUser(entityAudit.getCreatedBy());
            entityAuditDTO.setUserId(user_.getId());
            entityAuditDTO.setUserName(user_.getName());
            auditDTOs.add(entityAuditDTO);
        });
        return new PageRequestDTO<>(entityAudits.getTotalPages(), entityAudits.getTotalElements(), auditDTOs);
    }
}
