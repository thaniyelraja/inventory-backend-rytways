package com.project.irs_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Enter a valid email")
	private String email;

	@NotBlank(message = "Passowrd is required")
	private String password;

}
