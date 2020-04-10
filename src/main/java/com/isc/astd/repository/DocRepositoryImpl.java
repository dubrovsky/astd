package com.isc.astd.repository;

import com.isc.astd.domain.Doc;
import com.isc.astd.service.dto.MoreApprovedDTO;
import com.isc.astd.service.dto.MoreRejectedDTO;
import com.isc.astd.service.dto.MoreSignsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.sql.Timestamp;
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
    @SuppressWarnings("unchecked")
    public <T> T findDocsWithFilesToSign(Long nextSignPositionId, Long rootCatalogId, Integer start, Integer limit, Sort sort, boolean isCount) {
        Sort.Order order = null;
        if (!isCount) {
            order = sort.iterator().next();
        }

        final Query query = em.createNativeQuery(
                "SELECT\n" +
                        (!isCount ?
                                " ccc.name AS shCh,\n" +
                                        "  c.id AS catalogId,\n" +
                                        "  c.name AS stnLine,\n" +
                                        "  d.id AS docId,\n" +
                                        "  d.npp AS npp,\n" +
                                        "  d.num AS num,\n" +
                                        "  d.descr AS descr,\n" +
                                        "  f.id AS fileId,\n" +
                                        "  fp.last_modified_date AS dateSign,\n" +
                                        "  ff.listCount AS signNum,\n" +
                                        "  d.note_shl AS noteShl\n" :
                                " COUNT(*) AS count\n"
                        ) +
                        "FROM\n" +
                        "  doc d\n" +
                        "  JOIN catalog c\n" +
                        "    ON c.id = d.catalog_id\n" +
                        (rootCatalogId != null ? " AND d.root_cat_id = :rootCatalogId\n" : "") +
                        "  JOIN catalog cc\n" +
                        "    ON cc.id = c.parent_catalog_id\n" +
                        "  JOIN catalog ccc\n" +
                        "    ON ccc.id = cc.parent_catalog_id\n" +
                        "  JOIN file f\n" +
                        "    ON f.doc_id = d.id\n" +
                        "    AND f.next_sign_position_id = :nextSignPositionId\n" +
                        "    AND f.status IN ('default', 'signing')\n" +
                        "    AND NOT EXISTS (SELECT f3.id FROM `file` f3 WHERE f3.doc_id=d.id AND f3.status = 'rejected' AND f3.branch_type = 'default')\n" +
                        "  JOIN file_position fp\n" +
                        "    ON fp.file_id = f.id\n" +
                        "    AND fp.order =\n" +
                        "    (SELECT\n" +
                        "      MAX(fp3.order)\n" +
                        "    FROM\n" +
                        "      file_position fp3\n" +
                        "    WHERE fp3.file_id = fp.file_id)\n" +
                        "  JOIN\n" +
                        "    (SELECT\n" +
                        "      COUNT(*) AS listCount,\n" +
                        "      MAX(fp2.last_modified_date) AS max_date,\n" +
                        "      f2.doc_id\n" +
                        "    FROM\n" +
                        "      file f2\n" +
                        "      JOIN file_position fp2\n" +
                        "        ON fp2.file_id = f2.id\n" +
                        "        AND fp2.order =\n" +
                        "        (SELECT\n" +
                        "          MAX(fp3.order)\n" +
                        "        FROM\n" +
                        "          file_position fp3\n" +
                        "        WHERE fp3.file_id = fp2.file_id)\n" +
                        "      JOIN route_position rp2\n" +
                        "        ON rp2.route_id = f2.route_id\n" +
                        "        AND rp2.order = fp2.order + 1\n" +
                        "        AND rp2.status != 'assure'\n" +
                        "    WHERE f2.next_sign_position_id = :nextSignPositionId\n" +
                        "      AND f2.status IN ('default', 'signing')\n" +
                        "    GROUP BY f2.doc_id) ff\n" +
                        "    ON ff.doc_id = d.id\n" +
                        "    AND ff.max_date = fp.last_modified_date \n" +
                        (!isCount ? " ORDER BY " + order.getProperty() + " " + order.getDirection().name() : "")
                , Tuple.class
        );
        query.setParameter("nextSignPositionId", nextSignPositionId);
        if (rootCatalogId != null) {
            query.setParameter("rootCatalogId", rootCatalogId);
        }

        T result;
        if (!isCount) {
            query.setFirstResult(start).setMaxResults(limit);
            List<Tuple> tupleList = query.getResultList();
            result = (T) tupleList.stream().map(tuple -> new MoreSignsDTO(
                    String.valueOf(tuple.get("shCh") != null ? tuple.get("shCh") : ""),
                    ((BigInteger) tuple.get("catalogId")).longValue(),
                    String.valueOf(tuple.get("stnLine") != null ? tuple.get("stnLine") : ""),
                    ((BigInteger) tuple.get("docId")).longValue(),
                    ((BigInteger) tuple.get("npp")).longValue(),
                    String.valueOf(tuple.get("num") != null ? tuple.get("num") : ""),
                    String.valueOf(tuple.get("descr") != null ? tuple.get("descr") : ""),
                    ((BigInteger) tuple.get("fileId")).longValue(),
                    ((Timestamp) tuple.get("dateSign")).toInstant(),
                    ((BigInteger) tuple.get("signNum")).longValue(),
                    String.valueOf(tuple.get("noteShl") != null ? tuple.get("noteShl") : "")
            )).collect(Collectors.toList());
        } else {
            Tuple tuple = (Tuple) query.getSingleResult();
            result = (T) tuple.get("count");
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findDocsWithFilesAssureToSign(Long nextSignPositionId, Long rootCatalogId, Integer start, Integer limit, Sort sort, boolean isCount) {
        Sort.Order order = null;
        if (!isCount) {
            order = sort.iterator().next();
        }

        final Query query = em.createNativeQuery(
                "SELECT\n" +
                        (!isCount ?
                                "  ccc.name AS shCh,\n" +
                                        "  c.id AS catalogId,\n" +
                                        "  c.name AS stnLine,\n" +
                                        "  d.id AS docId,\n" +
                                        "  d.npp AS NPP,\n" +
                                        "  d.num AS NUM,\n" +
                                        "  d.descr AS DESCR,\n" +
                                        "  f.id AS fileId,\n" +
                                        "  fp.last_modified_date AS dateSign,\n" +
                                        "  ff.listCount AS signNum,\n" +
                                        "  d.note_shl AS noteShl\n" :
                                " COUNT(*) AS count\n"
                        ) +
                        "FROM\n" +
                        "  doc d\n" +
                        "  JOIN catalog c\n" +
                        "    ON c.id = d.catalog_id\n" +
                        (rootCatalogId != null ? " AND d.root_cat_id = :rootCatalogId\n" : "") +
                        "  JOIN catalog cc\n" +
                        "    ON cc.id = c.parent_catalog_id\n" +
                        "  JOIN catalog ccc\n" +
                        "    ON ccc.id = cc.parent_catalog_id\n" +
                        "  JOIN `file` f\n" +
                        "    ON f.doc_id = d.id\n" +
                        "    AND f.next_sign_position_id = :nextSignPositionId\n" +
                        "    AND f.status IN ('default', 'signing')\n" +
                        "    AND NOT EXISTS (SELECT f3.id FROM `file` f3 WHERE f3.doc_id=d.id AND f3.status = 'rejected' AND f3.branch_type = 'default')\n" +
                        "  JOIN file_position fp\n" +
                        "    ON fp.file_id = f.id\n" +
                        "    AND fp.order =\n" +
                        "    (SELECT\n" +
                        "      MAX(fp3.order)\n" +
                        "    FROM\n" +
                        "      file_position fp3\n" +
                        "    WHERE fp3.file_id = fp.file_id)\n" +
                        "  JOIN\n" +
                        "    (SELECT\n" +
                        "      COUNT(*) AS listCount,\n" +
                        "      MAX(fp2.last_modified_date) AS max_date,\n" +
                        "      f2.doc_id\n" +
                        "    FROM\n" +
                        "      `file` f2\n" +
                        "      JOIN file_position fp2\n" +
                        "        ON fp2.file_id = f2.id\n" +
                        "        AND fp2.order =\n" +
                        "        (SELECT\n" +
                        "          MAX(fp3.order)\n" +
                        "        FROM\n" +
                        "          file_position fp3\n" +
                        "        WHERE fp3.file_id = fp2.file_id)\n" +
                        "      JOIN route_position rp2\n" +
                        "        ON rp2.route_id = f2.route_id\n" +
                        "        AND rp2.order = fp2.order + 1\n" +
                        "        AND rp2.status = 'assure'\n" +
                        "    WHERE f2.next_sign_position_id = :nextSignPositionId\n" +
                        "      AND f2.status IN ('default', 'signing')\n" +
                        "    GROUP BY f2.doc_id) ff\n" +
                        "    ON ff.doc_id = d.id\n" +
                        "    AND ff.max_date = fp.last_modified_date \n" +
                        (!isCount ? " ORDER BY " + order.getProperty() + " " + order.getDirection().name() : "")
                , Tuple.class
        );
        query.setParameter("nextSignPositionId", nextSignPositionId);
        if (rootCatalogId != null) {
            query.setParameter("rootCatalogId", rootCatalogId);
        }

        T result;
        if (!isCount) {
            query.setFirstResult(start).setMaxResults(limit);
            List<Tuple> tupleList = query.getResultList();
            return (T) tupleList.stream().map(tuple -> new MoreSignsDTO(
                    String.valueOf(tuple.get("shCh") != null ? tuple.get("shCh") : ""),
                    ((BigInteger) tuple.get("catalogId")).longValue(),
                    String.valueOf(tuple.get("stnLine") != null ? tuple.get("stnLine") : ""),
                    ((BigInteger) tuple.get("docId")).longValue(),
                    ((BigInteger) tuple.get("npp")).longValue(),
                    String.valueOf(tuple.get("num") != null ? tuple.get("num") : ""),
                    String.valueOf(tuple.get("descr") != null ? tuple.get("descr") : ""),
                    ((BigInteger) tuple.get("fileId")).longValue(),
                    ((Timestamp) tuple.get("dateSign")).toInstant(),
                    ((BigInteger) tuple.get("signNum")).longValue(),
                    String.valueOf(tuple.get("noteShl") != null ? tuple.get("noteShl") : "")
            )).collect(Collectors.toList());
        } else {
            Tuple tuple = (Tuple) query.getSingleResult();
            result = (T) tuple.get("count");
        }

        return result;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T findDocsWithRejectedFiles(Long positionId, Long rootCatalogId, int start, int limit, Sort sort, boolean isCount) {
        Sort.Order order = null;
        if (!isCount) {
            order = sort.iterator().next();
        }

        final Query query = em.createNativeQuery(
                "SELECT\n" +
                        (!isCount ?
                                "  ccc.name AS shCh,\n" +
                                        "  c.id AS catalogId,\n" +
                                        "  c.name AS stnLine,\n" +
                                        "  d.id AS docId,\n" +
                                        "  d.npp AS npp,\n" +
                                        "  d.num AS num,\n" +
                                        "  d.descr AS descr,\n" +
                                        "  f.id AS fileId,\n" +
                                        "  fp.last_modified_date AS dateSign,\n" +
                                        "  ff.listCount AS listCount,\n" +
                                        "  d.note_shl AS noteShl,\n" +
                                        "  fp.msg AS msg\n" :
                                " COUNT(*) AS count\n"
                        ) +
                        "FROM\n" +
                        "  doc d\n" +
                        "  JOIN catalog c\n" +
                        "    ON c.id = d.catalog_id\n" +
                        (rootCatalogId != null ? " AND d.root_cat_id = :rootCatalogId\n" : "") +
                        "  JOIN catalog cc\n" +
                        "    ON cc.id = c.parent_catalog_id\n" +
                        "  JOIN catalog ccc\n" +
                        "    ON ccc.id = cc.parent_catalog_id\n" +
                        "  JOIN `file` f\n" +
                        "    ON f.doc_id = d.id\n" +
                        "    AND f.status = 'rejected'\n" +
                        "    AND f.branch_type = 'default'\n" +
                        "  JOIN file_position fp\n" +
                        "    ON fp.file_id = f.id\n" +
                        "    AND fp.order =\n" +
                        "    (SELECT\n" +
                        "      MAX(fp3.order)\n" +
                        "    FROM\n" +
                        "      file_position fp3\n" +
                        "    WHERE fp3.file_id = fp.file_id)\n" +
                        "  JOIN\n" +
                        "    (SELECT\n" +
                        "      COUNT(DISTINCT f2.id) AS listCount,\n" +
                        "      MAX(fp2.last_modified_date) AS max_date,\n" +
                        "      f2.doc_id\n" +
                        "    FROM\n" +
                        "      `file` f2\n" +
                        "      JOIN file_position fp2\n" +
                        "        ON fp2.file_id = f2.id\n" +
                        "        AND EXISTS\n" +
                        "        (SELECT\n" +
                        "          rp2.position_id\n" +
                        "        FROM\n" +
                        "          route_position rp2\n" +
                        "        WHERE rp2.route_id = f2.route_id\n" +
                        "          AND rp2.position_id = :positionId\n" +
                        "          AND rp2.order = fp2.order)\n" +
                        "    WHERE f2.status = 'rejected'\n" +
                        "      AND f2.branch_type = 'default'\n" +
                        "    GROUP BY f2.doc_id) ff\n" +
                        "    ON ff.doc_id = d.id\n" +
                        "    AND ff.max_date = fp.last_modified_date -- WHERE ccc.id=5 \n" +
                        (!isCount ? " ORDER BY " + order.getProperty() + " " + order.getDirection().name() : "")
                , Tuple.class
        );

        query.setParameter("positionId", positionId);
        if (rootCatalogId != null) {
            query.setParameter("rootCatalogId", rootCatalogId);
        }

        T result;
        if (!isCount) {
            query.setFirstResult(start).setMaxResults(limit);
            List<Tuple> tupleList = query.getResultList();
            return (T) tupleList.stream().map(tuple -> new MoreRejectedDTO(
                    String.valueOf(tuple.get("shCh") != null ? tuple.get("shCh") : ""),
                    ((BigInteger) tuple.get("catalogId")).longValue(),
                    String.valueOf(tuple.get("stnLine") != null ? tuple.get("stnLine") : ""),
                    ((BigInteger) tuple.get("docId")).longValue(),
                    ((BigInteger) tuple.get("npp")).longValue(),
                    String.valueOf(tuple.get("num") != null ? tuple.get("num") : ""),
                    String.valueOf(tuple.get("descr") != null ? tuple.get("descr") : ""),
                    ((BigInteger) tuple.get("fileId")).longValue(),
                    ((Timestamp) tuple.get("dateSign")).toInstant(),
                    ((BigInteger) tuple.get("listCount")).longValue(),
                    String.valueOf(tuple.get("noteShl") != null ? tuple.get("noteShl") : ""),
                    String.valueOf(tuple.get("msg") != null ? tuple.get("msg") : "")
            )).collect(Collectors.toList());
        } else {
            Tuple tuple = (Tuple) query.getSingleResult();
            result = (T) tuple.get("count");
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findDocsWithApprovedFiles(Long rootCatalogId, Integer start, Integer limit, Sort sort, boolean isCount) {
        Sort.Order order = null;
        if (!isCount) {
            order = sort.iterator().next();
        }

        final Query query = em.createNativeQuery(
                "SELECT\n" +
                        (!isCount ?
                                "  ccc.name AS shCh,\n" +
                                        "  c.id AS catalogId,\n" +
                                        "  c.name AS stnLine,\n" +
                                        "  d.id AS docId,\n" +
                                        "  d.npp AS npp,\n" +
                                        "  d.num AS num,\n" +
                                        "  d.descr AS descr,\n" +
                                        "  f.id AS fileId,\n" +
                                        "  fp.last_modified_date AS dateSign,\n" +
                                        "  f.paper_shl AS paperShL,\n" +
                                        "  f.paper_shchtd AS paperShChTD,\n" +
                                        "  (SELECT\n" +
                                        "    COUNT(*)\n" +
                                        "  FROM\n" +
                                        "    file ff\n" +
                                        "  WHERE ff.doc_id = d.id\n" +
                                        "    AND ff.branch_type = 'approved') AS listCount\n" :
                                " COUNT(*) AS count\n"
                        ) +
                        "FROM\n" +
                        "  doc d\n" +
                        "  JOIN catalog c\n" +
                        "    ON c.id = d.catalog_id\n" +
                        (rootCatalogId != null ? " AND d.root_cat_id = :rootCatalogId\n" : "") +
                        "  JOIN catalog cc\n" +
                        "    ON cc.id = c.parent_catalog_id\n" +
                        "  JOIN catalog ccc\n" +
                        "    ON ccc.id = cc.parent_catalog_id\n" +
                        "  JOIN file f\n" +
                        "    ON f.doc_id = d.id\n" +
                        "    AND f.branch_type = 'approved'\n" +
                        "    AND f.last_modified_date =\n" +
                        "    (SELECT\n" +
                        "      MAX(f2.last_modified_date)\n" +
                        "    FROM\n" +
                        "      file f2\n" +
                        "    WHERE f2.doc_id = f.doc_id\n" +
                        "      AND f2.branch_type = 'approved')\n" +
                        "  JOIN file_position fp\n" +
                        "    ON fp.file_id = f.id\n" +
                        "    AND fp.order =\n" +
                        "    (SELECT\n" +
                        "      MAX(fp2.order)\n" +
                        "    FROM\n" +
                        "      file_position fp2\n" +
                        "    WHERE fp2.file_id = fp.file_id) \n" +
                        (!isCount ? " ORDER BY " + order.getProperty() + " " + order.getDirection().name() : "")
                , Tuple.class
        );

        if (rootCatalogId != null) {
            query.setParameter("rootCatalogId", rootCatalogId);
        }

        T result;
        if (!isCount) {
            query.setFirstResult(start).setMaxResults(limit);
            List<Tuple> tupleList = query.getResultList();
            return (T) tupleList.stream().map(tuple -> new MoreApprovedDTO(
                    String.valueOf(tuple.get("shCh") != null ? tuple.get("shCh") : ""),
                    ((BigInteger) tuple.get("catalogId")).longValue(),
                    String.valueOf(tuple.get("stnLine") != null ? tuple.get("stnLine") : ""),
                    ((BigInteger) tuple.get("docId")).longValue(),
                    ((BigInteger) tuple.get("npp")).longValue(),
                    String.valueOf(tuple.get("num") != null ? tuple.get("num") : ""),
                    String.valueOf(tuple.get("descr") != null ? tuple.get("descr") : ""),
                    ((BigInteger) tuple.get("fileId")).longValue(),
                    ((Timestamp) tuple.get("dateSign")).toInstant(),
                    ((BigInteger) tuple.get("listCount")).longValue(),
                    (Boolean) tuple.get("paperShL"),
                    (Boolean) tuple.get("paperShChTD")
            )).collect(Collectors.toList());
        } else {
            Tuple tuple = (Tuple) query.getSingleResult();
            result = (T) tuple.get("count");
        }

        return result;
    }
}
