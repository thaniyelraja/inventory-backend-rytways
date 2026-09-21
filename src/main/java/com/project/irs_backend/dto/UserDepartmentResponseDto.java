package com.project.irs_backend.dto;

import com.project.irs_backend.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDepartmentResponseDto {

	private Long departmentId;
	private String departmentName;
	private String departmentCode;
	private Role role;

}
