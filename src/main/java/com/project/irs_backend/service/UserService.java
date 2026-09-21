package com.project.irs_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.irs_backend.dto.UserDepartmentDto;
import com.project.irs_backend.dto.UserDepartmentResponseDto;
import com.project.irs_backend.dto.UserRequestDto;
import com.project.irs_backend.dto.UserResponseDto;
import com.project.irs_backend.entity.Department;
import com.project.irs_backend.entity.Status;
import com.project.irs_backend.entity.User;
import com.project.irs_backend.entity.UserDepartment;
import com.project.irs_backend.enums.Role;
import com.project.irs_backend.repository.DepartmentRepository;
import com.project.irs_backend.repository.StatusRepository;
import com.project.irs_backend.repository.UserDepartmentRepository;
import com.project.irs_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final DepartmentRepository departmentRepository;

	private final StatusRepository statusRepository;

	private final UserDepartmentRepository userDepartmentRepository;

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	@Transactional
	public UserResponseDto updateUser(Long userId, UserRequestDto userRequest) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		if (userRequest.getDepartments() == null || userRequest.getDepartments().isEmpty()) {
			throw new RuntimeException("At least one department is required");
		}

		List<UserDepartment> existingDepartments = userDepartmentRepository.findByUser_UserId(userId);

		long requestedHodCount = userRequest.getDepartments().stream().filter(d -> Role.HOD.equals(d.getRole()))
				.count();

		if (requestedHodCount > 1) {
			throw new RuntimeException("A user can be HOD for only one department");
		}

		for (UserDepartmentDto departmentDto : userRequest.getDepartments()) {
			if (!Role.HOD.equals(departmentDto.getRole())) {
				continue;
			}
			long otherHodCount = userDepartmentRepository.countOtherHodsInDepartment(departmentDto.getDepartmentId(),
					Role.HOD.name(), userId);
			if (otherHodCount >= 2) {
				throw new RuntimeException("A department can have maximum 2 HODs");
			}
		}

		for (UserDepartment existing : existingDepartments) {
			if (!Role.HOD.equals(existing.getRole())) {
				continue;
			}
			Long departmentId = existing.getDepartment().getDepartmentId();
			boolean stillHod = userRequest.getDepartments().stream()
					.anyMatch(d -> departmentId.equals(d.getDepartmentId()) && Role.HOD.equals(d.getRole()));
			if (!stillHod) {
				long otherHodCount = userDepartmentRepository.countOtherHodsInDepartment(departmentId, Role.HOD.name(),
						userId);
				if (otherHodCount == 0) {
					throw new RuntimeException("Department must have at least one HOD");
				}
			}

		}
		boolean currentlyAdmin = existingDepartments.stream().anyMatch(d -> Role.ADMIN.equals(d.getRole()));
		boolean requestedAdmin = userRequest.getDepartments().stream().anyMatch(d -> Role.ADMIN.equals(d.getRole()));
		if (currentlyAdmin && !requestedAdmin) {
			long otherAdminCount = userDepartmentRepository.countByRoleIgnoreCaseAndUser_UserIdNot(Role.ADMIN, userId);
			if (otherAdminCount == 0) {
				throw new RuntimeException("At least one ADMIN is required");
			}
		}
		if (!currentlyAdmin && requestedAdmin) {
			long adminCount = userDepartmentRepository.countByRole(Role.ADMIN);
			if (adminCount > 0) {
				throw new RuntimeException("Only one ADMIN is allowed");
			}
		}

		user.setName(userRequest.getName());
		user.setEmail(userRequest.getEmail());

		if (userRequest.getPassword() != null && !userRequest.getPassword().trim().isEmpty()) {
			user.setPassword(userRequest.getPassword());
		}

		User savedUser = userRepository.save(user);

		userDepartmentRepository.deleteAll(existingDepartments);
		userDepartmentRepository.flush();
		List<UserDepartment> userDepartments = new ArrayList<>();

		for (UserDepartmentDto departmentDto : userRequest.getDepartments()) {

			Department department = departmentRepository.findById(departmentDto.getDepartmentId())
					.orElseThrow(() -> new RuntimeException("Department not found"));

			UserDepartment userDepartment = new UserDepartment();

			userDepartment.setUser(savedUser);
			userDepartment.setDepartment(department);
			userDepartment.setRole(departmentDto.getRole());

			UserDepartment savedUserDepartment = userDepartmentRepository.save(userDepartment);

			userDepartments.add(savedUserDepartment);
		}

		// Response
		UserResponseDto response = new UserResponseDto();

		BeanUtils.copyProperties(savedUser, response);

		List<UserDepartmentResponseDto> departments = userDepartments.stream()
				.map(ud -> new UserDepartmentResponseDto(ud.getDepartment().getDepartmentId(),
						ud.getDepartment().getDepartmentName(), ud.getDepartment().getDepartmentCode(), ud.getRole()))
				.toList();

		response.setDepartments(departments);

		return response;
	}

	@Transactional
	public void addDepartmentToUser(Long userId, UserDepartmentDto dto) {

		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

		Department department = departmentRepository.findById(dto.getDepartmentId())
				.orElseThrow(() -> new RuntimeException("Department not found"));

		boolean exists = userDepartmentRepository.existsByUser_UserIdAndDepartment_DepartmentId(userId,
				dto.getDepartmentId());

		if (exists) {
			throw new RuntimeException("User already in this department");
		}

		UserDepartment userDepartment = new UserDepartment();
		userDepartment.setUser(user);
		userDepartment.setDepartment(department);
		userDepartment.setRole(dto.getRole());
		userDepartmentRepository.save(userDepartment);
	}

	@Transactional
	public UserResponseDto createUser(UserRequestDto userRequest) {

		if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
			throw new RuntimeException("Email already exists");
		}

		if (userRequest.getDepartments() == null || userRequest.getDepartments().isEmpty()) {
			throw new RuntimeException("At leaset one department is required");
		}

		User user = new User();
		BeanUtils.copyProperties(userRequest, user);
		Status activeStatus = statusRepository.findByStatusCode("ACTIVE")
				.orElseThrow(() -> new RuntimeException("Active status not found"));
		user.setStatus(activeStatus);

		User savedUser = userRepository.save(user);

		List<UserDepartment> userDepartments = new ArrayList<>();

		for (UserDepartmentDto departmentDto : userRequest.getDepartments()) {

			boolean exists = userDepartmentRepository.existsByUser_UserIdAndDepartment_DepartmentId(
					savedUser.getUserId(), departmentDto.getDepartmentId());

			if (exists) {
				throw new RuntimeException("User already exists in this department");
			}

			Department department = departmentRepository.findById(departmentDto.getDepartmentId())
					.orElseThrow(() -> new RuntimeException("Department not found"));
			UserDepartment userDepartment = new UserDepartment();
			userDepartment.setUser(savedUser);
			userDepartment.setDepartment(department);
			userDepartment.setRole(departmentDto.getRole());

			UserDepartment savedUserDepartment = userDepartmentRepository.save(userDepartment);

			userDepartments.add(savedUserDepartment);
		}

		UserResponseDto response = new UserResponseDto();
		BeanUtils.copyProperties(savedUser, response);

		List<UserDepartmentResponseDto> departments = userDepartments.stream()
				.map(ud -> new UserDepartmentResponseDto(ud.getDepartment().getDepartmentId(),
						ud.getDepartment().getDepartmentName(), ud.getDepartment().getDepartmentCode(), ud.getRole()))
				.toList();

		response.setDepartments(departments);

		return response;

	}

	public Page<UserResponseDto> getUsers(String search, Pageable pageable) {
		return userRepository.searchUsers(search, pageable).map(user -> {
			UserResponseDto response = new UserResponseDto();
			BeanUtils.copyProperties(user, response);
			List<UserDepartmentResponseDto> departments = userDepartmentRepository.findByUser_UserId(user.getUserId())
					.stream()
					.map(ud -> new UserDepartmentResponseDto(ud.getDepartment().getDepartmentId(),
							ud.getDepartment().getDepartmentName(), ud.getDepartment().getDepartmentCode(),
							ud.getRole()))
					.toList();
			response.setDepartments(departments);
			return response;
		});
	}

}
