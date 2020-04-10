package com.isc.astd.service;

import com.isc.astd.service.dto.MoreApprovedDTO;
import com.isc.astd.service.dto.MoreRejectedDTO;
import com.isc.astd.service.dto.MoreSignsDTO;
import com.isc.astd.service.dto.PageRequestDTO;
import com.isc.astd.service.dto.PageableDTO;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

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

    public PageRequestDTO<MoreSignsDTO> getMoreSigns(PageableDTO pageableDTO, User user) throws IOException {
        return docService.getMoreSigns(pageableDTO, userService.getUser(user.getUsername()));
    }

    public PageRequestDTO<MoreSignsDTO> getMoreSignsAssure(PageableDTO pageableDTO, User user) throws IOException {
        return docService.getMoreSignsAssure(pageableDTO, userService.getUser(user.getUsername()));
    }

    public PageRequestDTO<MoreRejectedDTO> getMoreRejected(PageableDTO pageableDTO, User user) throws IOException {
        return docService.getMoreRejected(pageableDTO, userService.getUser(user.getUsername()));
    }

    public PageRequestDTO<MoreApprovedDTO> getMoreApproved(PageableDTO pageableDTO, User user) throws IOException {
        return docService.getMoreApproved(pageableDTO, userService.getUser(user.getUsername()));
    }

}
