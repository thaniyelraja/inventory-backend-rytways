package com.project.irs_backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.irs_backend.entity.User;
import com.project.irs_backend.enums.Role;
import com.project.irs_backend.enums.UserStatus;
import com.project.irs_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	public User createUser(User user) {

		return userRepository.save(user);

	}

	public Page<User> getUsers(String search, Pageable pageable) {
		return userRepository.searchUsers(search, pageable);
	}

}
