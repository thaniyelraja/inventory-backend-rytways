package com.project.irs_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email")
	private String email;

	@NotBlank(message = "Passowrd is required")
	@Size(min = 8, message = "password must be atleast 8 characters")
	private String password;

}
