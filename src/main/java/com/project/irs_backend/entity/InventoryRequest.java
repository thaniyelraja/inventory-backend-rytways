package com.project.irs_backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Transient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "status_id")
	private Status status;

	@CreationTimestamp
	@Column(name = "requested_at", updatable = false)
	private LocalDateTime requestedAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	@Column(name = "approval_remarks")
	private String approvalRemarks;

	@Column(name = "rejected_at")
	private LocalDateTime rejectedAt;

	@Column(name = "rejection_remarks")
	private String rejectionRemarks;

	@Column(name = "clarification_pending", nullable = false)
	private Boolean clarificationPending = false;
	
	@Column(name = "reminder_sent", nullable = false)
	private Boolean reminderSent = false;

	@OneToMany(mappedBy = "inventoryRequest", fetch = FetchType.EAGER)
	private List<InventoryRequestItem> items;

}
