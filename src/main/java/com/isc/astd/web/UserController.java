package com.isc.astd.web;

import com.isc.astd.service.UserService;
import com.isc.astd.service.dto.UserDTO;
import com.isc.astd.web.commons.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author p.dzeviarylin
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current")
    public ResponseEntity<Response<UserDTO>> getCurrentUser(@AuthenticationPrincipal User principal){
        UserDTO currentUserDTO = userService.getCurrentUser(principal);
        return ResponseEntity.ok(new Response<>(currentUserDTO));
    }
}
