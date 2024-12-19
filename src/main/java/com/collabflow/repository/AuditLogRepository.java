package com.collabflow.repository;

import com.collabflow.domain.AuditLog;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AuditLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Query("select auditLog from AuditLog auditLog where auditLog.user.login = ?#{authentication.name}")
    List<AuditLog> findByUserIsCurrentUser();
}
