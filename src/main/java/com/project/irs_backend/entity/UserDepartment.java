package com.project.irs_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.irs_backend.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_department", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "user_id", "department_id" }) })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDepartment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_department_id")
	private Long userDepartmentId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "department_id", nullable = false)
	private Department department;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

}
