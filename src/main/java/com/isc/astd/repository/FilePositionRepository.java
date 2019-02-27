package com.isc.astd.repository;

import com.isc.astd.domain.FilePosition;
import com.isc.astd.domain.FilePositionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface FilePositionRepository extends JpaRepository<FilePosition, FilePositionId> {
}
