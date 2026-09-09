package com.project.irs_backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.irs_backend.dto.UserResponseDto;
import com.project.irs_backend.entity.Department;
import com.project.irs_backend.entity.User;
import com.project.irs_backend.enums.Role;
import com.project.irs_backend.repository.UserRepository;
import com.project.irs_backend.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	private final UserRepository userRepository;

	@GetMapping("/users")
	public ResponseEntity<Page<UserResponseDto>> getUSers(@RequestParam(defaultValue = "") String search,
			Pageable pageable) {
		return ResponseEntity.ok(userService.getUsers(search, pageable));
	}

}
