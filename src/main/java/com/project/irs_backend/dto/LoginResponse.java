package com.project.irs_backend.dto;

import com.project.irs_backend.enums.Role;

import lombok.Data;

@Data
public class LoginResponse {

	private Long userId;

	private String name;

	private String email;

	private Role role;

}
