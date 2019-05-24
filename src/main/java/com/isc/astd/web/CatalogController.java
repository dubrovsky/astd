package com.isc.astd.web;

import com.isc.astd.service.CatalogService;
import com.isc.astd.service.dto.CatalogBaseDTO;
import com.isc.astd.service.dto.CatalogDTO;
import com.isc.astd.web.commons.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * @author p.dzeviarylin
 */
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/root")
    public ResponseEntity<Response<CatalogDTO>> getCatalogs(@AuthenticationPrincipal User principal){
        List<CatalogDTO> catalogDTOS = catalogService.getCatalogs(principal);
        return ResponseEntity.ok(new Response<>(catalogDTOS));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<CatalogDTO>> updateCatalog(@PathVariable("id") long id, @Valid @RequestBody CatalogDTO dto){
        List<CatalogDTO> catalogDTOS = catalogService.updateCatalog(id, dto);
        return ResponseEntity.ok(new Response<>(catalogDTOS));
    }

    @PostMapping()
    public ResponseEntity<Response<CatalogDTO>> createCatalog(@Valid @RequestBody CatalogDTO dto){
        List<CatalogDTO> catalogDTOS = catalogService.createCatalog(dto);
        return ResponseEntity.ok(new Response<>(catalogDTOS));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<CatalogDTO>> deleteCatalog(@PathVariable("id") long id) throws IOException {
        List<CatalogDTO> catalogDTOS = catalogService.deleteCatalog(id);
        return ResponseEntity.ok(new Response<>(catalogDTOS));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<CatalogDTO>> getCatalog(@PathVariable("id") long catalogId) {
        CatalogDTO catalogDTO = catalogService.getCatalogById(catalogId);
        return ResponseEntity.ok(new Response<>(catalogDTO));
    }

    @GetMapping()
    public ResponseEntity<Response<CatalogBaseDTO>> getAllRootCatalogs(){
        List<CatalogBaseDTO> catalogDTOS = catalogService.getAllRootCatalogs();
        return ResponseEntity.ok(new Response<>(catalogDTOS));
    }
}
