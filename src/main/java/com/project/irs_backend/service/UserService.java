package com.project.irs_backend.service;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.irs_backend.dto.UserRequestDto;
import com.project.irs_backend.dto.UserResponseDto;
import com.project.irs_backend.entity.User;
import com.project.irs_backend.enums.Role;
import com.project.irs_backend.repository.DepartmentRepository;
import com.project.irs_backend.repository.StatusRepository;
import com.project.irs_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final DepartmentRepository departmentRepository;

	private final StatusRepository statusRepository;

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	public UserResponseDto createUser(UserRequestDto userRequest) {

		User user = new User();
		BeanUtils.copyProperties(userRequest, user);

		user.setDepartment(departmentRepository.findById(userRequest.getDepartment().getDepartmentId())
				.orElseThrow(() -> new RuntimeException("Department not found")));

		user.setStatus(
				statusRepository.findById(1L).orElseThrow(() -> new RuntimeException("Active status not found")));

		User savedUser = userRepository.save(user);

		UserResponseDto response = new UserResponseDto();
		BeanUtils.copyProperties(savedUser, response);

		return response;

	}

	public Page<UserResponseDto> getUsers(String search, Pageable pageable) {
		return userRepository.searchUsers(search, pageable).map(user -> {
			UserResponseDto response = new UserResponseDto();
			BeanUtils.copyProperties(user, response);
			return response;
		});
	}

}
