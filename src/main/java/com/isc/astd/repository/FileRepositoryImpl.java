package com.isc.astd.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.isc.astd.domain.File;
import com.isc.astd.service.dto.FileSearchDTO;
import com.isc.astd.service.util.DomainUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FileRepositoryImpl implements FileRepositoryCustom {

    private final EntityManager em;

    private final DomainUtils domainUtils;

    public FileRepositoryImpl(EntityManager em, DomainUtils domainUtils) {
        this.em = em;
        this.domainUtils = domainUtils;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T searchFiles(Long rootCatalogId, Integer start, Integer limit, String sort, String filters, boolean isCount) throws JsonProcessingException {
        final Query query = em.createNativeQuery(
                "SELECT\n" +
                        (!isCount ?
                                "  ccc.name AS rootCatalogName,\n" +
                                        "  c.id AS docCatalogId,\n" +
                                        "  c.name AS docCatalogName,\n" +
                                        "  d.id AS docId,\n" +
                                        "  d.npp AS docNpp,\n" +
                                        "  d.num AS docNum,\n" +
                                        "  d.descr AS docDescr,\n" +
                                        "  f.descr AS descr,\n" +
                                        "  f.id AS id,\n" +
                                        "  f.branch_type AS branchType,\n" +
                                        "  f.status AS status,\n" +
                                        "  f.list_num AS listNum\n" :
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
                        "WHERE \n" +
                        domainUtils.getFilters(filters, "f.") +
                        (!isCount ? " ORDER BY " + domainUtils.getSorts(sort, "f.last_modified_date", "desc") : ""),
                Tuple.class
        );

        if (rootCatalogId != null) {
            query.setParameter("rootCatalogId", rootCatalogId);
        }

        T result;
        if (!isCount) {
            query.setFirstResult(start).setMaxResults(limit);
            List<Tuple> tupleList = query.getResultList();
            return (T) tupleList.stream().map(tuple -> new FileSearchDTO(
                    tuple.get("rootCatalogName") != null ? tuple.get("rootCatalogName", String.class) : "",
                    ((BigInteger) tuple.get("docCatalogId")).longValue(),
                    tuple.get("docCatalogName") != null ? tuple.get("docCatalogName", String.class) : "",
                    ((BigInteger) tuple.get("docId")).longValue(),
                    ((BigInteger) tuple.get("docNpp")).longValue(),
                    tuple.get("docNum") != null ? tuple.get("docNum", String.class) : "",
                    tuple.get("docDescr") != null ? tuple.get("docDescr", String.class) : "",
                    tuple.get("descr") != null ? tuple.get("descr", String.class) : "",
                    ((BigInteger) tuple.get("id")).longValue(),
                    File.BranchType.valueOf(tuple.get("branchType", String.class).toUpperCase()),
                    File.Status.valueOf(tuple.get("status", String.class).toUpperCase()),
                    tuple.get("listNum") != null ? tuple.get("listNum", String.class) : "")
            ).collect(Collectors.toList());
        } else {
            Tuple tuple = (Tuple) query.getSingleResult();
            result = (T) tuple.get("count");
        }

        return result;
    }

}
