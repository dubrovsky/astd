package com.isc.astd.service;

import com.isc.astd.domain.Position;
import com.isc.astd.repository.PositionRepository;
import com.isc.astd.service.dto.PositionDTO;
import com.isc.astd.service.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class PositionService {

    private final PositionRepository positionRepository;

    private final Mapper mapper;

    public PositionService(PositionRepository positionRepository, Mapper mapper) {
        this.positionRepository = positionRepository;
        this.mapper = mapper;
    }

    public Position getPosition(long id) {
        return positionRepository.findById(id).orElseThrow(() -> new RuntimeException("Position not found"));
    }

    @Transactional(readOnly = true)
    public List<PositionDTO> getAllPositions() {
        return mapper.mapAsList(positionRepository.findAll(), PositionDTO.class);
    }
}
