package com.isc.astd.service;

import com.isc.astd.domain.User;
import com.isc.astd.repository.UserRepository;
import com.isc.astd.service.dto.UserDTO;
import com.isc.astd.service.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final Mapper mapper;

    public UserService(UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    public User getUser(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(org.springframework.security.core.userdetails.User principal) {
        User user = getUser(principal.getUsername());
        return mapper.map(user, UserDTO.class);
    }
}
