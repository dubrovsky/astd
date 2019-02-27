package com.isc.astd.web;

import com.isc.astd.service.MainService;
import com.isc.astd.service.dto.MoreSignsDTO;
import com.isc.astd.web.commons.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<Response<MoreSignsDTO>> getAllFiles(@AuthenticationPrincipal User principal){
        List<MoreSignsDTO> moreSigns = mainService.getMoreSigns(principal);
        return ResponseEntity.ok(new Response<>(moreSigns));
    }
}
