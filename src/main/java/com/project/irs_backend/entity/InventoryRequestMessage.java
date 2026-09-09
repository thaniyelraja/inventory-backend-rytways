package com.project.irs_backend.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "inventory_request_message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "message_id")
	private Long messageId;

	@ManyToOne
	@JoinColumn(name = "inventory_request_id")
	private InventoryRequest inventoryRequest;

	@ManyToOne
	@JoinColumn(name = "sender_user_id")
	private User senderUser;

	@CreationTimestamp
	@Column(name = "sended_at", updatable = false)
	private LocalDateTime sendedAt;

	@Column(nullable = false)
	private String message;

}
