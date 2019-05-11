package com.isc.astd.service;

import com.isc.astd.service.dto.MoreRejectedDTO;
import com.isc.astd.service.dto.MoreSignsDTO;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author p.dzeviarylin
 */
@Service
@Transactional
public class MainService {

    private final DocService docService;

    private final UserService userService;

    public MainService(DocService docService, UserService userService) {
        this.docService = docService;
        this.userService = userService;
    }

    public List<MoreSignsDTO> getMoreSigns(User user){
        return docService.getMoreSigns(userService.getUser(user.getUsername()));
    }

	public List<MoreRejectedDTO> getMoreRejected(User user){
		return docService.getMoreRejected(userService.getUser(user.getUsername()));
	}

}
