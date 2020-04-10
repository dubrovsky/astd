package com.isc.astd.repository;

import com.isc.astd.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    Page<User> findAllByExpiredDateIsNull(Pageable pageable);
}
