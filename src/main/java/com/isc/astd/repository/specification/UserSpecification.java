package com.isc.astd.repository.specification;

import com.isc.astd.domain.User;
import com.isc.astd.service.dto.UserFilterDTO;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
public class UserSpecification {

    public static Specification<User> filter(UserFilterDTO filterDTO) {

        return (Specification<User>) (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.isNull(root.get("expiredDate")));

            if(filterDTO.getCatalogId() != null && filterDTO.getCatalogId() > 0){
                predicates.add(builder.equal(root.get("rootCatalog").get("id"), filterDTO.getCatalogId()));
            }

            if(filterDTO.getPositionId() != null && filterDTO.getPositionId() > 0){
                predicates.add(builder.equal(root.get("position").get("id"), filterDTO.getPositionId()));
            }

            return builder.and(predicates.toArray(new Predicate[] {}));
        };
    }
}
