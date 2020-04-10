package com.isc.astd.service;

import com.isc.astd.domain.Catalog;
import com.isc.astd.domain.File;
import com.isc.astd.service.dto.CatalogDTO;
import com.isc.astd.service.mapper.Mapper;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author p.dzeviarylin
 */
@Service
public class VirtualCatalogService {

    private final Mapper mapper;

    public VirtualCatalogService(Mapper mapper) {
        this.mapper = mapper;
    }

    CatalogDTO addVirtualCatalog(CatalogDTO parentCatalogDTO, Catalog.Type type, File.BranchType branchType) {
        Set<CatalogDTO> childrenCopy;
        CatalogDTO virtualCatalog = new CatalogDTO(parentCatalogDTO.getId(), type, type.generateId(parentCatalogDTO.getId()), parentCatalogDTO.getId(), branchType);
        if(!parentCatalogDTO.getChildren().isEmpty()) {
            childrenCopy = mapper.mapAsSet(parentCatalogDTO.getChildren(), CatalogDTO.class);
            childrenCopy.forEach(child -> updateVirtualCatalog(virtualCatalog, child, type, branchType, true));
            virtualCatalog.setChildren(new TreeSet<>(childrenCopy));
        }
        return virtualCatalog;
    }

    private void updateVirtualCatalog(CatalogDTO parentCatalogDTO, CatalogDTO catalogDTO, Catalog.Type type, File.BranchType branchType, boolean deep) {
        catalogDTO.setParentCatalogId(parentCatalogDTO.getId());
        catalogDTO.setId(type.generateId(catalogDTO.getId()));
        catalogDTO.setBranchType(branchType);
//        catalogDTO.setType(type);
        catalogDTO.setReadOnly(true);
        catalogDTO.setLevel(catalogDTO.getLevel() + 1);
        if(deep) {
            catalogDTO.getChildren().forEach(child -> updateVirtualCatalog(catalogDTO, child, type, branchType, deep));
        }
    }

    List<CatalogDTO> addVirtualCatalogsToCatalog(Catalog catalog, Catalog parentCatalog) {
        CatalogDTO catalogDTO = mapper.map(catalog, CatalogDTO.class);
//        CatalogDTO parentCatalogDTO = mapper.map(parentCatalog, CatalogDTO.class);
        List<CatalogDTO> catalogDTOS = new ArrayList<>(1);
        catalogDTOS.add(catalogDTO);
//        catalogDTOS.addAll(createVirtualCatalogs(catalog, parentCatalogDTO));
        return catalogDTOS;
    }

    /*private List<CatalogDTO> createVirtualCatalogs(Catalog catalog, CatalogDTO parentCatalogDTO) {
        CatalogDTO archiveCatalogDTO = mapper.map(catalog, CatalogDTO.class);
        updateVirtualCatalog(parentCatalogDTO, archiveCatalogDTO, Catalog.Type.ARCHIVE, File.BranchType.ARCHIVE, false);
        CatalogDTO rejectedCatalogDTO = mapper.map(catalog, CatalogDTO.class);
        updateVirtualCatalog(parentCatalogDTO, rejectedCatalogDTO, Catalog.Type.REJECTED, File.BranchType.REJECTED, false);
        return Arrays.asList(archiveCatalogDTO, rejectedCatalogDTO);
    }*/
}
