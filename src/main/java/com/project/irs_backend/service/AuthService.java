package com.project.irs_backend.service;

import org.springframework.stereotype.Service;

import com.project.irs_backend.entity.User;
import com.project.irs_backend.enums.UserStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserService userService;

	public User login(String email, String password) {
		User user = userService.findByEmail(email);

		if (user == null) {
			throw new RuntimeException("User not found");
		}
		if (!user.getPassword().equals(password)) {
			throw new RuntimeException("Invalid email or password");
		}
		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new RuntimeException("User account is inactive");
		}
		return user;

	}

}
