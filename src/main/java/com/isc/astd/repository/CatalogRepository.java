package com.isc.astd.repository;

import com.isc.astd.domain.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    List<Catalog> findAllByType(Catalog.Type type);
}
