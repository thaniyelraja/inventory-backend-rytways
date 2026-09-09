package com.project.irs_backend.dto;

import com.project.irs_backend.entity.Department;
import com.project.irs_backend.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

	@NotBlank(message = "Name is required")
	@Size(min = 3, max = 50, message = "Name must be between 3 - 50 characters")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "Password is required")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must containe atleast 8 characters, one uppercase, one number and one special character")
	private String password;

	@NotNull(message = "Role is required")
	private Role role;

	@NotNull(message = "Department is required")
	private Department department;

}
