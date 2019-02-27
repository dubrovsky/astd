package com.isc.astd.repository;

import com.isc.astd.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
}
