package com.isc.astd.service;

import com.isc.astd.domain.Position;
import com.isc.astd.repository.PositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class PositionService {

    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
    }

    public Position getPosition(long id) {
        return positionRepository.findById(id).orElseThrow(() -> new RuntimeException("Position not found"));
    }
}
