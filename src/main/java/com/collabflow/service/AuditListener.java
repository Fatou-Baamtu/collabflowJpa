package com.collabflow.service;

import com.collabflow.domain.enumeration.ActionEnum;
import com.collabflow.domain.enumeration.EntityEnum;
import com.collabflow.security.SecurityUtils;
import com.collabflow.service.dto.AuditLogDTO;
import com.collabflow.service.dto.UserDTO;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class AuditListener {

    private static final Logger LOG = LoggerFactory.getLogger(AuditListener.class);

    // ApplicationContext pour récupérer les beans de Spring
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        AuditListener.applicationContext = applicationContext;
    }

    private AuditLogService getAuditLogService() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext is not initialized!");
        }
        return applicationContext.getBean(AuditLogService.class);
    }

    private UserService getUserService() {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext is not initialized!");
        }
        return applicationContext.getBean(UserService.class);
    }

    private Long extractId(Object entity) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return (Long) idField.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Impossible d'extraire l'ID de l'entité : " + entity.getClass().getSimpleName(), e);
        }
    }

    // Méthode générique pour l'enregistrement de l'audit
    private void logAudit(String action, Object entity) {
        LOG.debug("Request to save logAudit : {}", action, entity);

        Long entityId = extractId(entity);
        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setEntity(EntityEnum.valueOf(entity.getClass().getSimpleName()));
        auditLogDTO.setEntityId(entityId);
        auditLogDTO.setAction(ActionEnum.valueOf(action));
        auditLogDTO.setTimestamp(Instant.now());
        // Récupérer le login de l'utilisateur actuel et créer un UserDTO
        SecurityUtils.getCurrentUserLogin()
            .ifPresentOrElse(
                login -> {
                    // Récupérer l'utilisateur complet
                    Optional<UserDTO> optionalUserDTO = getUserService().findByLogin(login);
                    if (optionalUserDTO.isPresent()) {
                        UserDTO userDTO = optionalUserDTO.get();
                        auditLogDTO.setUser(userDTO);
                        LOG.debug("User assigned to audit log: {}", userDTO);
                    } else {
                        LOG.warn("No user found in database for login: {}", login);
                    }
                },
                () -> LOG.warn("No user found in security context")
            );
        getAuditLogService().save(auditLogDTO); // Obtenir le service à la demande
    }

    @PrePersist
    public void onCreate(Object entity) {
        LOG.info("PrePersist called for entity: {}", entity.getClass().getSimpleName());
        logAudit("CREATED", entity);
    }

    @PreUpdate
    public void onUpdate(Object entity) {
        LOG.info("PreUpdate called for entity: {}", entity.getClass().getSimpleName());
        logAudit("UPDATED", entity);
    }

    @PreRemove
    public void onDelete(Object entity) {
        LOG.info("PreRemove called for entity: {}", entity.getClass().getSimpleName());
        logAudit("DELETED", entity);
    }
}
