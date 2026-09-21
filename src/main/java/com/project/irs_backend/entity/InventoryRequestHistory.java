package com.project.irs_backend.entity;

import java.time.LocalDateTime;

import com.project.irs_backend.enums.HistoryAction;

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
@Table(name = "inventory_request_history")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "history_id")
	private Long historyId;

	@ManyToOne
	@JoinColumn(name = "inventory_request_id")
	private InventoryRequest inventoryRequest;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private HistoryAction action;

	private LocalDateTime createdAt;

}
