package com.isc.astd.web;

import com.isc.astd.domain.File;
import com.isc.astd.service.DocService;
import com.isc.astd.service.dto.DocDTO;
import com.isc.astd.service.dto.DocSearchDTO;
import com.isc.astd.service.dto.PageRequestDTO;
import com.isc.astd.service.dto.DomainPageParamsDTO;
import com.isc.astd.web.commons.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collections;

/**
 * @author p.dzeviarylin
 */
@RestController
@RequestMapping("/api")
public class DocController {

    private final DocService docService;

    public DocController(DocService docService) {
        this.docService = docService;
    }

    @GetMapping("/doc")
    public ResponseEntity<Response<DocDTO>> getAllDocs(@RequestParam long catalogId, @RequestParam File.BranchType branchType, DomainPageParamsDTO domainPageParamsDTO, @AuthenticationPrincipal User user) throws IOException {
        PageRequestDTO<DocDTO> pageRequestDTO = docService.getAllDocs(catalogId, branchType, user, domainPageParamsDTO);
        return ResponseEntity.ok(new Response<>(pageRequestDTO.getContent(), pageRequestDTO.getTotalElements()));
    }

    @GetMapping("/searchdoc")
    public ResponseEntity<Response<DocSearchDTO>> searchDocs(DomainPageParamsDTO domainPageParamsDTO, @AuthenticationPrincipal User user) throws IOException {
        PageRequestDTO<DocSearchDTO> searchDocs = docService.searchDocs(domainPageParamsDTO, user);
        return ResponseEntity.ok(new Response<>(searchDocs.getContent(), searchDocs.getTotalElements()));
    }

    @PutMapping("/doc/{id}")
    public ResponseEntity updateDoc(@PathVariable("id") long id, @Valid @RequestBody DocDTO dto){
        docService.updateDoc(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/doc")
    public ResponseEntity<Response<DocDTO>> createDoc(@Valid @RequestBody DocDTO dto, @AuthenticationPrincipal User principal){
        DocDTO newDoc = docService.createDoc(dto, principal);
        return ResponseEntity.ok(new Response<>(Collections.singletonList(newDoc)));
    }

    @DeleteMapping("/doc/{id}")
    public ResponseEntity deleteDoc(@PathVariable("id") long id) throws IOException {
        docService.deleteDoc(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/doc/{id}")
    public ResponseEntity<Response<DocDTO>> getDoc(@PathVariable("id") long docId, @RequestParam File.BranchType branchType, @AuthenticationPrincipal User principal) {
        DocDTO docDTO = docService.getDocById(docId, principal, branchType);
        return ResponseEntity.ok(new Response<>(docDTO));
    }
}
