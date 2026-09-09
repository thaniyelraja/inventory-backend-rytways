package com.project.irs_backend.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.irs_backend.dto.LoginRequest;
import com.project.irs_backend.dto.LoginResponse;
import com.project.irs_backend.dto.UserRequestDto;
import com.project.irs_backend.dto.UserResponseDto;
import com.project.irs_backend.entity.Department;
import com.project.irs_backend.entity.User;
import com.project.irs_backend.enums.Role;
import com.project.irs_backend.service.AuthService;
import com.project.irs_backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	private final UserService userService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

		User user = authService.login(request.getEmail(), request.getPassword());

		LoginResponse response = new LoginResponse();

		BeanUtils.copyProperties(user, response);

		return ResponseEntity.ok(response);

	}

	@PostMapping("/create-user")
	public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto user) {
		return ResponseEntity.ok(userService.createUser(user));
	}

	@GetMapping("/roles")
	public ResponseEntity<Role[]> getRoles() {
		return ResponseEntity.ok(Role.values());
	}

	@GetMapping("/departments")
	public ResponseEntity<List<Department>> getDepartments() {
		return ResponseEntity.ok(authService.getDepartments());
	}	

}
