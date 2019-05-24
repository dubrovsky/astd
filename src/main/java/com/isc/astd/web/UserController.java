package com.isc.astd.web;

import com.isc.astd.service.UserService;
import com.isc.astd.service.dto.PageRequestDTO;
import com.isc.astd.service.dto.PageableDTO;
import com.isc.astd.service.dto.UserBaseDTO;
import com.isc.astd.service.dto.UserDTO;
import com.isc.astd.web.commons.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collections;

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

	@GetMapping()
	public ResponseEntity<Response<UserDTO>> getAllUsers(PageableDTO pageableDTO, @AuthenticationPrincipal User principal) {
		PageRequestDTO<UserDTO> allUsers = userService.getAllUsers(principal, pageableDTO);
		return ResponseEntity.ok(new Response<>(allUsers.getContent(), allUsers.getTotalElements()));
	}

    @GetMapping("/current")
    public ResponseEntity<Response<UserDTO>> getCurrentUser(@AuthenticationPrincipal User principal){
        UserDTO currentUserDTO = userService.getCurrentUser(principal);
        return ResponseEntity.ok(new Response<>(currentUserDTO));
    }

	@PutMapping("/{id}")
	public ResponseEntity updateUser(@PathVariable("id") String id, @Valid @RequestBody UserBaseDTO dto){
		userService.updateUser(id, dto);
		return ResponseEntity.ok().build();
	}

	@PostMapping()
	public ResponseEntity<Response<UserBaseDTO>> createUser(@Valid @RequestBody UserBaseDTO dto){
		UserBaseDTO newUser = userService.createUser(dto);
		return ResponseEntity.ok(new Response<>(Collections.singletonList(newUser)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deleteUser(@PathVariable("id") String id) {
		userService.deleteUser(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Response<UserBaseDTO>> getUser(@PathVariable("id") String id) {
		UserBaseDTO dto = userService.getUserById(id);
		return ResponseEntity.ok(new Response<>(dto));
	}
}
