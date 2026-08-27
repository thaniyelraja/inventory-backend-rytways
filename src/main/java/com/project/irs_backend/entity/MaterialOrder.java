package com.project.irs_backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.project.irs_backend.enums.OrderStatus;

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
@Table(name = "material_order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "material_order_id")
	private Long materialOrderId;

	@ManyToOne
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@Column(name = "order_date_time")
	private LocalDateTime orderDateTime;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status")
	private OrderStatus orderStatus;

	@Column(name = "total_amount")
	private BigDecimal totalAmount;

	@Column(name = "created_by")
	private Long createdBy;

}
