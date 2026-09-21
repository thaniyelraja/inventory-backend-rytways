package com.project.irs_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.irs_backend.dto.LoginResponse;
import com.project.irs_backend.dto.UserDepartmentResponseDto;
import com.project.irs_backend.entity.Department;
import com.project.irs_backend.entity.Status;
import com.project.irs_backend.entity.User;
import com.project.irs_backend.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserService userService;

	private final DepartmentRepository departmentRepository;

	public LoginResponse login(String email, String password) {
		User user = userService.findByEmail(email);

		if (user == null) {
			throw new RuntimeException("User not found");
		}
		if (!user.getPassword().equals(password)) {
			throw new RuntimeException("Invalid email or password");
		}

		if (!"ACTIVE".equals(user.getStatus().getStatusCode())) {
			throw new RuntimeException("User account is inactive");
		}
		List<UserDepartmentResponseDto> departments = user.getUserDepartments().stream()
				.map(ud -> new UserDepartmentResponseDto(ud.getDepartment().getDepartmentId(),
						ud.getDepartment().getDepartmentName(), ud.getDepartment().getDepartmentCode(), ud.getRole()))
				.toList();

		LoginResponse response = new LoginResponse();
		response.setUserId(user.getUserId());
		response.setName(user.getName());
		response.setEmail(user.getEmail());
		response.setStatus(user.getStatus());
		response.setDepartments(departments);

		return response;

	}

	public List<Department> getDepartments() {

		return departmentRepository.findAll();

	}

}
