package com.isc.astd.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.isc.astd.config.WebConfiguration;
import com.isc.astd.domain.Doc;
import com.isc.astd.service.dto.DocSearchDTO;
import com.isc.astd.service.dto.MoreApprovedDTO;
import com.isc.astd.service.dto.MoreRejectedDTO;
import com.isc.astd.service.dto.MoreSignsDTO;
import com.isc.astd.service.util.DomainUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final DomainUtils domainUtils;

    private final Logger log = LoggerFactory.getLogger(DocRepositoryImpl.class);

    @Autowired
    public DocRepositoryImpl(JpaContext context, DomainUtils domainUtils) {
        this.em = context.getEntityManagerByManagedType(Doc.class);
        this.domainUtils = domainUtils;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findDocsWithFilesToSign(Long nextSignPositionId, Long rootCatalogId, Integer start, Integer limit, /*Sort sort,*/String sort, boolean isCount) throws JsonProcessingException {
        String q;
        final Query query = em.createNativeQuery(
                q = "SELECT\n" +
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
                        "    AND f.status IN ('default', 'signing') AND f.branch_type='default'\n" +
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
                        "      AND f2.status IN ('default', 'signing') AND f2.branch_type='default'\n" +
                        "    GROUP BY f2.doc_id) ff\n" +
                        "    ON ff.doc_id = d.id\n" +
                        "    AND ff.max_date = fp.last_modified_date \n" +
                        (!isCount ? " ORDER BY " + domainUtils.getSorts(sort, "dateSign", "asc") : "")
                , Tuple.class
        );
        query.setParameter("nextSignPositionId", nextSignPositionId);
        if (rootCatalogId != null) {
            query.setParameter("rootCatalogId", rootCatalogId);
        }

//        log.debug(q);


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
    public <T> T findDocsWithFilesAssureToSign(Long nextSignPositionId, Long rootCatalogId, Integer start, Integer limit, String sort, boolean isCount) throws JsonProcessingException {
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
                        "    AND f.status IN ('default', 'signing') AND f.branch_type='default'\n" +
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
                        "      AND f2.status IN ('default', 'signing') AND f2.branch_type='default'\n" +
                        "    GROUP BY f2.doc_id) ff\n" +
                        "    ON ff.doc_id = d.id\n" +
                        "    AND ff.max_date = fp.last_modified_date \n" +
                        (!isCount ? " ORDER BY " + domainUtils.getSorts(sort, "dateSign", "asc") : "")
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
    public <T> T searchDocs(Long rootCatalogId, Integer start, Integer limit, String sort, String filters, boolean isCount) throws JsonProcessingException {
        final Query query = em.createNativeQuery(
                "SELECT\n" +
                        (!isCount ?
                                "  ccc.name AS rootCatalogName,\n" +
                                        "  c.id AS docCatalogId,\n" +
                                        "  c.name AS docCatalogName,\n" +
                                        "  d.id AS id,\n" +
                                        "  d.npp AS npp,\n" +
                                        "  d.num AS num,\n" +
                                        "  d.descr AS descr,\n" +
                                        "  (SELECT\n" +
                                        "    COUNT(*)\n" +
                                        "  FROM\n" +
                                        "    `file` f2\n" +
                                        "  WHERE f2.doc_id = d.id\n" +
                                        "    AND f2.branch_type = 'default') AS filesDefaultCount,\n" +
                                        "  (SELECT\n" +
                                        "    COUNT(*)\n" +
                                        "  FROM\n" +
                                        "    `file` f2\n" +
                                        "  WHERE f2.doc_id = d.id\n" +
                                        "    AND f2.branch_type = 'approved') AS filesApprovedCount,\n" +
                                        "  (SELECT\n" +
                                        "    COUNT(*)\n" +
                                        "  FROM\n" +
                                        "    `file` f2\n" +
                                        "  WHERE f2.doc_id = d.id\n" +
                                        "    AND f2.branch_type = 'archive') AS filesArchiveCount,\n" +
                                        "  (SELECT\n" +
                                        "    COUNT(*)\n" +
                                        "  FROM\n" +
                                        "    `file` f2\n" +
                                        "  WHERE f2.doc_id = d.id) AS filesAllCount\n" :
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
                        "WHERE \n" +
                        domainUtils.getFilters(filters, "d.") +
                        (!isCount ? " ORDER BY " + domainUtils.getSorts(sort, "d.last_modified_date", "desc") : ""),
                Tuple.class
        );

        if (rootCatalogId != null) {
            query.setParameter("rootCatalogId", rootCatalogId);
        }

        T result;
        if (!isCount) {
            query.setFirstResult(start).setMaxResults(limit);
            List<Tuple> tupleList = query.getResultList();
            return (T) tupleList.stream().map(tuple -> new DocSearchDTO(
                    ((BigInteger) tuple.get("id")).longValue(),
                    ((BigInteger) tuple.get("docCatalogId")).longValue(),
                    ((BigInteger) tuple.get("npp")).longValue(),
                    String.valueOf(tuple.get("num") != null ? tuple.get("num") : ""),
                    String.valueOf(tuple.get("descr") != null ? tuple.get("descr") : ""),
                    String.valueOf(tuple.get("rootCatalogName") != null ? tuple.get("rootCatalogName") : ""),
                    String.valueOf(tuple.get("docCatalogName") != null ? tuple.get("docCatalogName") : ""),
                    ((BigInteger) tuple.get("filesDefaultCount")).longValue(),
                    ((BigInteger) tuple.get("filesApprovedCount")).longValue(),
                    ((BigInteger) tuple.get("filesArchiveCount")).longValue(),
                    ((BigInteger) tuple.get("filesAllCount")).longValue()
            )).collect(Collectors.toList());
        } else {
            Tuple tuple = (Tuple) query.getSingleResult();
            result = (T) tuple.get("count");
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findDocsWithRejectedFiles(Long positionId, Long rootCatalogId, Integer start, Integer limit, String sort, boolean isCount) throws JsonProcessingException {
        String q = null;
        final Query query = em.createNativeQuery(
                q = "SELECT\n" +
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
                        "          AND rp2.position_id = :positionId)\n" +
                        "    WHERE f2.status = 'rejected'\n" +
                        "      AND f2.branch_type = 'default'\n" +
                        "    GROUP BY f2.doc_id) ff\n" +
                        "    ON ff.doc_id = d.id\n" +
                        "    AND ff.max_date = fp.last_modified_date -- WHERE ccc.id=5 \n" +
                        (!isCount ? " ORDER BY " + domainUtils.getSorts(sort, "dateSign", "desc") : "")
                , Tuple.class
        );

//        log.debug(q);

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
    public <T> T findDocsWithApprovedFiles(Long rootCatalogId, Integer start, Integer limit, String sort, boolean isCount) throws JsonProcessingException {

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
                                        "  WHERE ff.doc_id = d.id AND ff.route_id IN (1, 2, 5)\n" +
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
                        "    ON f.doc_id = d.id AND f.route_id IN (1, 2, 5)\n" +
                        "    AND f.branch_type = 'approved'\n" +
                        "    AND f.last_modified_date =\n" +
                        "    (SELECT\n" +
                        "      MAX(f2.last_modified_date)\n" +
                        "    FROM\n" +
                        "      file f2\n" +
                        "    WHERE f2.doc_id = f.doc_id AND f2.route_id IN (1, 2, 5)\n" +
                        "      AND f2.branch_type = 'approved')\n" +
                        "  JOIN file_position fp\n" +
                        "    ON fp.file_id = f.id\n" +
                        "    AND fp.order =\n" +
                        "    (SELECT\n" +
                        "      MAX(fp2.order)\n" +
                        "    FROM\n" +
                        "      file_position fp2\n" +
                        "    WHERE fp2.file_id = fp.file_id) \n" +
                        (!isCount ? " ORDER BY " + domainUtils.getSorts(sort, "dateSign", "desc") : "")
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
