package com.isc.astd.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isc.astd.service.dto.FilterDTO;
import com.isc.astd.service.dto.SortDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author p.dzeviarylin
 */
@Component
public class DomainUtils {

    private final ObjectMapper objectMapper;

    public DomainUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Sort getSort(String jsonSort, String defaultProperty, Sort.Direction defaultDirection) throws IOException {
        if (StringUtils.isNotBlank(jsonSort)) {
            SortDTO[] sortDTOS = objectMapper.readValue(jsonSort, SortDTO[].class);
            return Sort.by(Sort.Direction.valueOf(sortDTOS[0].getDirection()), sortDTOS[0].getProperty());
        } else {
            return Sort.by(defaultDirection != null ? defaultDirection : Sort.Direction.ASC, defaultProperty != null ? defaultProperty : "id");
        }
    }

    public Sort getSortUnsafe(String jsonSort, String defaultProperty) throws IOException {
        if (StringUtils.isNotBlank(jsonSort)) {
            SortDTO[] sortDTOS = objectMapper.readValue(jsonSort, SortDTO[].class);
            return JpaSort.unsafe(Sort.Direction.valueOf(sortDTOS[0].getDirection()), "CAST(" + sortDTOS[0].getProperty() + " AS int)", sortDTOS[0].getProperty());
        } else {
            return JpaSort.unsafe(Sort.Direction.ASC, "CAST(" + (defaultProperty != null ? defaultProperty : "id") + " AS int)", defaultProperty != null ? defaultProperty : "id");
        }
    }

    public String getSorts(String jsonSort, String defaultProperty, String defaultDirection) throws JsonProcessingException {
        final StringBuilder sortsBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(jsonSort)) {
            final SortDTO[] sorts = objectMapper.readValue(jsonSort, SortDTO[].class);
            int sortCount = 0;
            for (SortDTO sort : sorts) {
                if (sortCount > 0) {
                    sortsBuilder.append(", ");
                }
                sortsBuilder.append(sort.getProperty()).append(" ").append(sort.getDirection());
                sortCount++;
            }
        } else {
            sortsBuilder.append(defaultProperty != null ? defaultProperty : "id").append(" ").append(defaultDirection != null ? defaultDirection : "desc");
        }
        return sortsBuilder.toString();
    }

    public String getFilters(String jsonFilters, String prefix) throws JsonProcessingException {
        if (StringUtils.isNotBlank(jsonFilters)) {
            final FilterDTO[] filters = objectMapper.readValue(jsonFilters, FilterDTO[].class);
            final StringBuilder filtersBuilder = new StringBuilder();
            int filterCount = 0;
            for (FilterDTO filter : filters) {
                if (StringUtils.isNotBlank(filter.getValue())) {
                    if (filterCount > 0) {
                        filtersBuilder.append(" AND ");
                    }
                    filtersBuilder.
                            append(prefix).
                            append(filter.getProperty()).
                            append(getFilterOperator(filter.getOperator())).
                            append(getFilterValue(filter.getValue(), filter.getOperator()));
                    filterCount++;
                }
            }
            return filtersBuilder.toString();
        }
        return null;
    }

    private String getFilterValue(String value, String operator) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNumeric(value)) {
            sb.append(value);
        } else { // string
            sb.append("'");
            if (operator.equals("like")) {
                sb.append("%").append(value).append("%");
            } else {
                sb.append(value);
            }
            sb.append("'");
        }
        return sb.toString();
    }

    private String getFilterOperator(String operator) {
        if (operator.equals("like")) {
            return " LIKE ";
        }
        return operator;
    }

    /*
    *ORDER BY CAST(list_num AS UNSIGNED),list_num
ORDER BY CAST(list_num AS UNSIGNED) DESC,list_num DESC
    * */
}
