package com.project.irs_backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.project.irs_backend.enums.RequestStatus;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory_request")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inventory_request_id")
	private Long inventoryRequestId;

	@Column(name = "request_quantity")
	private Integer requestQuantity;

	@Column(name = "approved_quantity")
	private Integer approvedQuantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "request_status")
	private RequestStatus requestStatus;

	@CreationTimestamp
	@Column(name = "requested_at", updatable = false)
	private LocalDateTime requestedAt;

	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	@Column(name = "approval_remarks")
	private String approvalRemarks;
	
	@Column(name = "rejected_at")
	private LocalDateTime rejectedAt;

	@Column(name = "rejection_remarks")
	private String rejectionRemarks;

	@ManyToOne
	@JoinColumn(name = "material_id")
	private Material material;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

}
