package com.isc.astd.service;

import com.isc.astd.domain.Audit;
import com.isc.astd.domain.User;
import com.isc.astd.repository.UserRepository;
import com.isc.astd.service.dto.PageRequestDTO;
import com.isc.astd.service.dto.PageableDTO;
import com.isc.astd.service.dto.UserBaseDTO;
import com.isc.astd.service.dto.UserDTO;
import com.isc.astd.service.mapper.Mapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PositionService positionService;
    private final CatalogService catalogService;

    private final Mapper mapper;

    public UserService(UserRepository userRepository, @Lazy PositionService positionService, @Lazy CatalogService catalogService, Mapper mapper) {
        this.userRepository = userRepository;
		this.positionService = positionService;
		this.catalogService = catalogService;
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

	public PageRequestDTO<UserDTO> getAllUsers(org.springframework.security.core.userdetails.User principal, PageableDTO pageableDTO) {
		Page<User> users = userRepository.findAll(
				PageRequest.of(pageableDTO.getPage() - 1, pageableDTO.getLimit(), new Sort(Sort.Direction.DESC, "id"))
		);
		return new PageRequestDTO<>(users.getTotalPages(), users.getTotalElements(), mapper.mapAsList(users, UserDTO.class));
	}

	public UserBaseDTO createUser(UserBaseDTO dto) {
		User user = mapper.map(dto, User.class);
		user.setId(user.getId().replaceAll("\\s",""));
		user.setPosition(positionService.getPosition(dto.getPositionId()));
		if(dto.getRootCatalogId() != null){
			user.setRootCatalog(catalogService.getCatalog(dto.getRootCatalogId()));
		}
		user.setAuditAction(Audit.Action.USER_CREATE);
		user = userRepository.save(user);
		return mapper.map(user, UserBaseDTO.class);
	}

	public UserBaseDTO updateUser(String id, UserBaseDTO dto) {
		User user;
    	if(!id.equals(dto.getPrevId())){
			deleteUser(dto.getPrevId()); // it's not possible to change PK id
			user = mapper.map(dto, User.class);
		} else {
			user = getUser(id);
			mapper.map(dto, user);
		}

		user.setId(user.getId().replaceAll("\\s",""));
		user.setPosition(positionService.getPosition(dto.getPositionId()));
		if(dto.getRootCatalogId() != null){
			user.setRootCatalog(catalogService.getCatalog(dto.getRootCatalogId()));
		} else {
			user.setRootCatalog(null);
		}
		user.setAuditAction(Audit.Action.USER_UPDATE);
		user = userRepository.save(user);
		return mapper.map(user, UserBaseDTO.class);
	}

	public void deleteUser(String id) {
		User user = getUser(id);
		user.setAuditAction(Audit.Action.USER_DELETE);
		userRepository.delete(user);
	}

	public UserBaseDTO getUserById(String id) {
		User user = getUser(id);
		return mapper.map(user, UserBaseDTO.class);
	}
}
