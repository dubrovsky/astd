package com.isc.astd.service;

import com.isc.astd.domain.Audit;
import com.isc.astd.domain.Catalog;
import com.isc.astd.domain.User;
import com.isc.astd.repository.CatalogRepository;
import com.isc.astd.service.dto.CatalogBaseDTO;
import com.isc.astd.service.dto.CatalogDTO;
import com.isc.astd.service.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class CatalogService {

    private final CatalogRepository catalogRepository;

    private final Mapper mapper;

    private final FileSystemService fileSystemService;

    private final VirtualCatalogService virtualCatalogService;

    private final UserService userService;

    public CatalogService(CatalogRepository catalogRepository, Mapper mapper, FileSystemService fileSystemService, VirtualCatalogService virtualCatalogService, UserService userService) {
        this.catalogRepository = catalogRepository;
        this.mapper = mapper;
        this.fileSystemService = fileSystemService;
        this.virtualCatalogService = virtualCatalogService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<CatalogDTO> getCatalogs(org.springframework.security.core.userdetails.User principal) {

//        List<CatalogDTO> catalogDTOS = mapper.mapAsList(catalogRepository.findAllByType(Catalog.Type.ROOT), CatalogDTO.class);

        User user = userService.getUser(principal.getUsername());
        List<CatalogDTO> catalogDTOS;
        if(user.getRootCatalog() != null) {
            catalogDTOS = mapper.mapAsList(catalogRepository.findAllById(Collections.singleton(user.getRootCatalog().getId())), CatalogDTO.class);
        }
        else {
            catalogDTOS = mapper.mapAsList(catalogRepository.findAllByType(Catalog.Type.ROOT), CatalogDTO.class);
        }

        /*catalogDTOS.forEach(shch -> {
            CatalogDTO stationOrLine = shch.getChildren().iterator().next();
            stationOrLine.getChildren().stream().sorted(Co);
            stationOrLine = shch.getChildren().iterator().next();
        });*/

/*
        catalogDTOS.forEach(catalogDTO0 -> catalogDTO0.getChildren().forEach(catalogDTO1 -> {
            CatalogDTO archiveCatalog = virtualCatalogService.addVirtualCatalog(catalogDTO1, Catalog.Type.ARCHIVE, File.BranchType.ARCHIVE);
            CatalogDTO rejectedCatalog = virtualCatalogService.addVirtualCatalog(catalogDTO1, Catalog.Type.REJECTED, File.BranchType.REJECTED);
            catalogDTO1.getChildren().add(archiveCatalog);
            catalogDTO1.getChildren().add(rejectedCatalog);
        }));
*/

        return catalogDTOS;
    }

    public List<CatalogDTO> updateCatalog(long id, CatalogDTO dto) {
        Catalog catalog = getCatalog(id);
        mapper.map(dto, catalog);
        catalog.setAuditAction(Audit.Action.CATALOG_UPDATE);
        catalog = save(catalog);

        Catalog parentCatalog = getCatalog(dto.getParentCatalogId());
        return virtualCatalogService.addVirtualCatalogsToCatalog(catalog, parentCatalog);
//        return mapper.map(catalog, CatalogDTO.class);
    }

    public Catalog save(Catalog catalog) {
        return catalogRepository.save(catalog);
    }

    public Catalog getCatalog(long id) {
        return catalogRepository.findById(id).orElseThrow(() -> new RuntimeException("Catalog not found"));
    }

    public List<CatalogDTO> createCatalog(CatalogDTO dto) {
        Catalog catalog = mapper.map(dto, Catalog.class);
        catalog.setAuditAction(Audit.Action.CATALOG_CREATE);
        catalog = save(catalog);

        Catalog parentCatalog = getCatalog(dto.getParentCatalogId());
        if(!parentCatalog.isReadOnly()) {
            parentCatalog.setReadOnly(true);
            save(catalog);
        }

        return virtualCatalogService.addVirtualCatalogsToCatalog(catalog, parentCatalog);
    }

    public List<CatalogDTO> deleteCatalog(long id) throws IOException {
        Catalog catalog = getCatalog(id);
        Catalog parentCatalog = catalog.getParentCatalog();
        catalog.setAuditAction(Audit.Action.CATALOG_DELETE);
        catalogRepository.delete(catalog);
        catalogRepository.flush();

        if(!parentCatalog.isReadOnlyByDefault() && parentCatalog.getChildCatalogs().isEmpty()){
            parentCatalog.setReadOnly(false);
            save(parentCatalog);
        }

        fileSystemService.deleteCatalog(catalog);

        return virtualCatalogService.addVirtualCatalogsToCatalog(catalog, parentCatalog);
    }

    public Catalog getParentCatalog(Catalog catalog, long parentId){
        while(catalog.getId() != parentId){
            catalog = catalog.getParentCatalog();
        }
        return catalog;
    }

    public Catalog getRootCatalog(Catalog catalog){
        while(catalog.getType() != Catalog.Type.ROOT){
            catalog = catalog.getParentCatalog();
        }
        return catalog;
    }

    public CatalogDTO getCatalogById(long catalogId) {
        Catalog catalog = getCatalog(catalogId);
        CatalogDTO catalogDTO = mapper.map(catalog, CatalogDTO.class);
        /*while(catalog.getParentCatalog() != null){
            catalogDTO.setP
        }*/
        return catalogDTO;
    }

    @Transactional(readOnly = true)
    public List<CatalogBaseDTO> getAllRootCatalogs() {
        return mapper.mapAsList(catalogRepository.findAllByType(Catalog.Type.ROOT), CatalogBaseDTO.class);
    }
}
