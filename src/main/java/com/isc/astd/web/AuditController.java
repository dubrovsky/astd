package com.isc.astd.web;

import com.isc.astd.service.AuditService;
import com.isc.astd.service.dto.AuditDTO;
import com.isc.astd.service.dto.PageRequestDTO;
import com.isc.astd.service.dto.DomainPageParamsDTO;
import com.isc.astd.web.commons.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author p.dzeviarylin
 */
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService entityAuditService;

    public AuditController(AuditService entityAuditService) {
        this.entityAuditService = entityAuditService;
    }

    @GetMapping()
    public ResponseEntity<Response<AuditDTO>> getAllEntities(DomainPageParamsDTO domainPageParamsDTO, @AuthenticationPrincipal User user){
        PageRequestDTO<AuditDTO> pageRequestDTO = entityAuditService.getAllEntities(user, domainPageParamsDTO);
        return ResponseEntity.ok(new Response<>(pageRequestDTO.getContent(), pageRequestDTO.getTotalElements()));
    }
}
