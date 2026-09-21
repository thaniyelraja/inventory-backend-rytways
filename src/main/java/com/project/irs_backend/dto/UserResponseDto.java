package com.project.irs_backend.dto;

import java.util.List;

import com.project.irs_backend.entity.Department;
import com.project.irs_backend.entity.Status;
import com.project.irs_backend.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

	private Long userId;

	private String name;

	private String email;

	private Status status;

	private List<UserDepartmentResponseDto> departments;

}
