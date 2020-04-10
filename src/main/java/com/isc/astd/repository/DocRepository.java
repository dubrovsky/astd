package com.isc.astd.repository;

import com.isc.astd.domain.Doc;
import com.isc.astd.service.dto.DocDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Repository
public interface DocRepository extends JpaRepository<Doc, Long>, JpaSpecificationExecutor<Doc>, DocRepositoryCustom {
    List<Doc> findAllByCatalogId(long catalogId);
//    List<Doc> findAllByCatalogIdAndBranchType(long catalogId, Doc.BranchType branchType);

    @Query(value =
            "SELECT\n" +
            "  d.id AS id, d.catalog.id AS catalogId, d.npp AS npp, d.descr AS descr, COUNT(f.id) AS signNum\n" +
            "FROM\n" +
            "  Doc d\n" +
            "  JOIN d.files f\n" +
            "WHERE f.nextSignPosition.id = :nextSignPositionId\n" +
            "  AND f.id NOT IN\n" +
            "  (SELECT\n" +
            "    fp.id.file.id\n" +
            "  FROM\n" +
            "    FilePosition fp\n" +
            "  WHERE fp.createdBy = :userId)\n" +
            "  GROUP BY d.id"
    )
    Collection<DocDTO> findDocsWithFilesToSign(@Param("nextSignPositionId") String nextSignPositionId, @Param("userId") long userId);

    long countAllByCreatedByEqualsOrLastModifiedByEquals(String user1, String user2);
}
