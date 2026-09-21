package com.project.irs_backend.dto;

import java.util.List;

import com.project.irs_backend.entity.Status;
import com.project.irs_backend.enums.Role;

import lombok.Data;

@Data
public class LoginResponse {

	private Long userId;

	private String name;

	private String email;

	private Status status;

	private List<UserDepartmentResponseDto> departments;

}
