package com.isc.astd.web;

import com.isc.astd.service.MainService;
import com.isc.astd.service.dto.MoreApprovedDTO;
import com.isc.astd.service.dto.MoreRejectedDTO;
import com.isc.astd.service.dto.MoreSignsDTO;
import com.isc.astd.service.dto.PageRequestDTO;
import com.isc.astd.service.dto.PageableDTO;
import com.isc.astd.web.commons.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author p.dzeviarylin
 */
@RestController
@RequestMapping("/api")
public class MainController {

    private final MainService mainService;

    public MainController(MainService mainService) {
        this.mainService = mainService;
    }

    @GetMapping("/moresigns")
    public ResponseEntity<Response<MoreSignsDTO>> getMoreSigns(PageableDTO pageableDTO, @AuthenticationPrincipal User principal) throws IOException {
        PageRequestDTO<MoreSignsDTO> moreSigns = mainService.getMoreSigns(pageableDTO, principal);
        return ResponseEntity.ok(new Response<>(moreSigns.getContent(), moreSigns.getTotalElements()));
    }

    @GetMapping("/moresignsassure")
    public ResponseEntity<Response<MoreSignsDTO>> getMoreSignsAssure(PageableDTO pageableDTO, @AuthenticationPrincipal User principal) throws IOException {
        PageRequestDTO<MoreSignsDTO> moreSigns = mainService.getMoreSignsAssure(pageableDTO, principal);
        return ResponseEntity.ok(new Response<>(moreSigns.getContent(), moreSigns.getTotalElements()));
    }

    @GetMapping("/morerejected")
    public ResponseEntity<Response<MoreRejectedDTO>> getMoreRejected(PageableDTO pageableDTO, @AuthenticationPrincipal User principal) throws IOException {
        PageRequestDTO<MoreRejectedDTO> moreRejected = mainService.getMoreRejected(pageableDTO, principal);
        return ResponseEntity.ok(new Response<>(moreRejected.getContent(), moreRejected.getTotalElements()));
    }

    @GetMapping("/moreapproved")
    public ResponseEntity<Response<MoreApprovedDTO>> getMoreApproved(PageableDTO pageableDTO, @AuthenticationPrincipal User principal) throws IOException {
        PageRequestDTO<MoreApprovedDTO> moreApproved = mainService.getMoreApproved(pageableDTO, principal);
        return ResponseEntity.ok(new Response<>(moreApproved.getContent(), moreApproved.getTotalElements()));
    }
}
