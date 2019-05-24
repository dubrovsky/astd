package com.isc.astd.web;

import com.isc.astd.service.PositionService;
import com.isc.astd.service.dto.PositionDTO;
import com.isc.astd.web.commons.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@RestController
@RequestMapping("/api/position")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping()
    public ResponseEntity<Response<PositionDTO>> getAllPositions(){
        List<PositionDTO> positionDTOS = positionService.getAllPositions();
        return ResponseEntity.ok(new Response<>(positionDTOS));
    }
}
