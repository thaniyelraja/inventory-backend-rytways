package com.project.irs_backend.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.irs_backend.dto.LoginRequest;
import com.project.irs_backend.dto.LoginResponse;
import com.project.irs_backend.dto.UserDepartmentDto;
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

		return ResponseEntity.ok(authService.login(request.getEmail(), request.getPassword()));

	}

	@PostMapping("/create-user")
	public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto user) {
		return ResponseEntity.ok(userService.createUser(user));
	}

	@PutMapping("/update/{userId}")
	public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long userId,
			@RequestBody UserRequestDto userRequest) {
		return ResponseEntity.ok(userService.updateUser(userId, userRequest));
	}

	@PostMapping("/{userId}/departments")
	public ResponseEntity<String> addDepartments(@PathVariable Long userId, @RequestBody UserDepartmentDto dto) {
		userService.addDepartmentToUser(userId, dto);
		return ResponseEntity.ok("Department added successfully");
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
