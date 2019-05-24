package com.isc.astd.repository;

import com.isc.astd.domain.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
public interface AuditRepository extends JpaRepository<Audit, Long> {

    List<Audit> findAllByEntityTypeAndEntityId(String entityType, Long entityId);

    Page<Audit> findAllByEntityTypeAndEntityId(String entityType, Long entityId, Pageable pageRequest);

    @Query("SELECT max(a.commitVersion) FROM Audit a where a.entityType = :type and a.entityId = :entityId")
    Integer findMaxCommitVersion(@Param("type") String type, @Param("entityId") String entityId);

    @Query("SELECT DISTINCT (a.entityType) from Audit a")
    List<String> findAllEntityTypes();

    Page<Audit> findAllByEntityType(String entityType, Pageable pageRequest);

    @Query("SELECT ae FROM Audit ae where ae.entityType = :type and ae.entityId = :entityId and " +
            "ae.commitVersion =(SELECT max(a.commitVersion) FROM Audit a where a.entityType = :type and " +
            "a.entityId = :entityId and a.commitVersion < :commitVersion)")
    Audit findOneByEntityTypeAndEntityIdAndCommitVersion(@Param("type") String type, @Param("entityId") Long entityId, @Param("commitVersion") Integer commitVersion);
}
