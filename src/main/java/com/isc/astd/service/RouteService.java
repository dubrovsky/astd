package com.isc.astd.service;

import com.isc.astd.domain.Route;
import com.isc.astd.repository.RouteRepository;
import com.isc.astd.service.dto.RouteDTO;
import com.isc.astd.service.mapper.Mapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class RouteService {

    private final RouteRepository routeRepository;

    private final UserService userService;

    private final Mapper mapper;

	public RouteService(RouteRepository routeRepository, UserService userService, Mapper mapper) {
        this.routeRepository = routeRepository;
		this.userService = userService;
		this.mapper = mapper;
	}

    @Transactional(readOnly = true)
    public List<RouteDTO> getAllRoutes(User user) {

        return mapper.mapAsList(routeRepository.findAllByPositionAndExpiredDateIsNull(userService.getUser(user.getUsername()).getPosition()), RouteDTO.class);
    }

    public Route getRoute(long id) {
        return routeRepository.findById(id).orElseThrow(() -> new RuntimeException("Route not found"));
    }
}
