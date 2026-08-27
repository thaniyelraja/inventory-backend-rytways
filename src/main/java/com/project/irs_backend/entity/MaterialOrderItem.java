package com.project.irs_backend.entity;

import java.math.BigDecimal;

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
@Table(name = "material_order_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialOrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "material_order_item_id")
	private Long materialOrderItemId;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private MaterialOrder order;

	@ManyToOne
	@JoinColumn(name = "material_id")
	private Material material;

	private Long quantity;

	private BigDecimal price;

	@Column(name = "total_price")
	private BigDecimal totalPrice;

}
