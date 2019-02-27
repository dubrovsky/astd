package com.isc.astd.repository.specification;

import com.isc.astd.domain.Doc;
import com.isc.astd.domain.File;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
public class DocSpecification {
    public static Specification<Doc> byCatalogIdAndBranchType(long catalogId, File.BranchType branchType) {
        return (Specification<Doc>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("catalog").get("id"), catalogId));
/*
            switch (branchType){
                case ARCHIVE:
                    predicates.add(criteriaBuilder.equal(root.get("archive"), true));
                    break;
                case REJECTED:
                    predicates.add(criteriaBuilder.equal(root.get("rejected"), true));
                    break;
            }
*/
            return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
        };
    }

    /*public static Specification<Doc> byCatalogIdAndBranchType1() {
        return (Specification<Doc>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            Subquery<FilePosition> filePositionSubquery = query.subquery(FilePosition.class);
            final Root<FilePosition> filePositionRoot = filePositionSubquery.from(FilePosition.class);
            filePositionSubquery.select(filePositionRoot);

            final Join<Doc, File> file = root.join("File");
            predicates.add(
                criteriaBuilder.equal(file.get("nextSignPosition").get("id"), "")
            );

            return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
        };
    }*/
}
