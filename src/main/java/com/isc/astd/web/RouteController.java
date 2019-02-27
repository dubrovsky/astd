package com.isc.astd.web;

import com.isc.astd.service.dto.RouteDTO;
import com.isc.astd.service.RouteService;
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
@RequestMapping("/api/route")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping()
    public ResponseEntity<Response<RouteDTO>> getAllRoutes(@AuthenticationPrincipal User user){
        List<RouteDTO> routeDTOS = routeService.getAllRoutes(user);
        return ResponseEntity.ok(new Response<>(routeDTOS));
    }
}
