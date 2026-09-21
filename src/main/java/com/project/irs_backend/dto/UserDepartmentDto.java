package com.project.irs_backend.dto;

import com.project.irs_backend.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDepartmentDto {

	private Long departmentId;
	private Role role;

}
