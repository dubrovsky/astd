package com.isc.astd.service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isc.astd.service.dto.PageableDTO;
import com.isc.astd.service.dto.SortDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
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

    public Sort getSort(PageableDTO pageableDTO) throws IOException {
        if(StringUtils.isNotBlank(pageableDTO.getSort())) {
            SortDTO[] sortDTOS = objectMapper.readValue(pageableDTO.getSort(), SortDTO[].class);
            return new Sort(Sort.Direction.valueOf(sortDTOS[0].getDirection()), sortDTOS[0].getProperty());
        } else {
            return new Sort(Sort.Direction.ASC, "id");
        }
    }
}
