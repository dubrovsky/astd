package com.isc.astd.repository;

import com.isc.astd.domain.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface FileRepository extends JpaRepository<File, Long>, JpaSpecificationExecutor<File>, FileRepositoryCustom {
    List<File> findAllByDocIdAndBranchTypeInAndListNumAndStatusNotIn(long docId, Collection<File.BranchType> branchTypes, String listNum, Collection<File.Status> status);

    List<File> findAllByDocIdAndBranchTypeInAndListNumAndIdNotAndStatusNotIn(long docId, Collection<File.BranchType> branchTypes, String listNum, Long id, Collection<File.Status> status);

    @Query("select f from File f where f.doc.id = ?1 and branchType = ?2")
    Page<File> findByByDocIdAndBranchType(long docId, File.BranchType branchType, Pageable pageable);

    long countAllByCreatedByEqualsOrLastModifiedByEquals(String user1, String user2);

    List<File> findAllByIdIsIn(List<Long> ids);
}


