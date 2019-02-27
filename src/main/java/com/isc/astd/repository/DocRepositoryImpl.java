package com.isc.astd.repository;

import com.isc.astd.domain.Doc;
import com.isc.astd.service.dto.MoreSignsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author p.dzeviarylin
 */
@Repository
public class DocRepositoryImpl implements DocRepositoryCustom {

    private final EntityManager em;
    /*
     @PersistenceContext
    protected EntityManager em;
    * */

    @Autowired
    public DocRepositoryImpl(JpaContext context) {
        this.em = context.getEntityManagerByManagedType(Doc.class);
    }

    @Override
    public List<MoreSignsDTO> findDocsWithFilesToSign(long nextSignPositionId, String userId, Long rootCatalogId) {
        // Criteria crit = em.unwrap(Session.class).createCriteria(Foo.class);
        final Query query = em.createQuery(
                "SELECT\n" +
                "  d.id AS docId, d.catalog.id AS catalogId, d.npp AS npp, d.num AS num, d.descr AS descr, f.id as fileId \n" +
//                         ", COUNT(f.id) AS signNum\n" +
                "FROM\n" +
                "  Doc d\n" +
                "  JOIN d.files f\n" +
                "WHERE f.nextSignPosition.id = :nextSignPositionId AND f.status IN ('default', 'signing') \n" +
                "  AND NOT EXISTS\n" +
                "  (SELECT\n" +
                "    fp.id.file.id\n" +
                "  FROM\n" +
                "    FilePosition fp\n" +
                "  WHERE fp.id.file.id = f.id AND fp.createdBy = :userId)\n" +
                (rootCatalogId != null ? " AND d.rootCatalog.id = :rootCatalogId\n" : "")
//              +  "  GROUP BY d.id",
                , Tuple.class
        );
        query.setParameter("nextSignPositionId", nextSignPositionId);
        query.setParameter("userId", userId);
        if(rootCatalogId != null){
            query.setParameter("rootCatalogId", rootCatalogId);
        }
        List<Tuple> tupleList = query.getResultList();
        return tupleList.stream().map(tuple -> new MoreSignsDTO(
                (long)tuple.get("docId"), (long)tuple.get("catalogId"), (long)tuple.get("npp"), String.valueOf(tuple.get("num")), String.valueOf(tuple.get("descr")), /*(long)tuple.get("signNum"), */
                (long)tuple.get("fileId"))).collect(Collectors.toList());
    }
}
