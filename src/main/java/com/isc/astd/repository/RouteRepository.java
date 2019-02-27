package com.isc.astd.repository;

import com.isc.astd.domain.Position;
import com.isc.astd.domain.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
	List<Route> findAllByPosition(Position position);
}
