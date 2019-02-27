package com.isc.astd.repository.specification;

import com.isc.astd.domain.File;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
public class FileSpecification {

    public static Specification<File> byDocIdAndBranchType(long docId, File.BranchType branchType) {
        return (Specification<File>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("doc").get("id"), docId));
            predicates.add(criteriaBuilder.equal(root.get("branchType"), branchType));
            return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
        };
    }
}
