package com.isc.astd.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isc.astd.service.dto.PageableDTO;
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
public class Utils {

    private final ObjectMapper objectMapper;

    public Utils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Sort getSort(PageableDTO pageableDTO, String defaultProperty, Sort.Direction defaultDirection) throws IOException {
        if (StringUtils.isNotBlank(pageableDTO.getSort())) {
            SortDTO[] sortDTOS = objectMapper.readValue(pageableDTO.getSort(), SortDTO[].class);
            return Sort.by(Sort.Direction.valueOf(sortDTOS[0].getDirection()), sortDTOS[0].getProperty());
        } else {
            return Sort.by(defaultDirection != null ? defaultDirection : Sort.Direction.ASC, defaultProperty != null ? defaultProperty : "id");
        }
    }

    public Sort getSortUnsafe(PageableDTO pageableDTO, String defaultProperty) throws IOException {
        if (StringUtils.isNotBlank(pageableDTO.getSort())) {
            SortDTO[] sortDTOS = objectMapper.readValue(pageableDTO.getSort(), SortDTO[].class);
            return JpaSort.unsafe(Sort.Direction.valueOf(sortDTOS[0].getDirection()), "CAST(" + sortDTOS[0].getProperty() + " AS int)", sortDTOS[0].getProperty());
        } else {
//            return new Sort(Sort.Direction.ASC, defaultProperty != null ? defaultProperty : "id");
            return JpaSort.unsafe(Sort.Direction.ASC, "CAST(" + (defaultProperty != null ? defaultProperty : "id") + " AS int)", defaultProperty != null ? defaultProperty : "id");
        }
    }

    /*
    *ORDER BY CAST(list_num AS UNSIGNED),list_num
ORDER BY CAST(list_num AS UNSIGNED) DESC,list_num DESC
    * */
}
