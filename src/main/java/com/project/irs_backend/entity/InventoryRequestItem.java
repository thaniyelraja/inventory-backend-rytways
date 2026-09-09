package com.project.irs_backend.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "inventory_request_item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inventory_request_item_id")
	private Long inventoryRequestItemId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "inventory_request_id")
	private InventoryRequest inventoryRequest;

	@ManyToOne
	@JoinColumn(name = "inventory_id")
	private Inventory inventory;

	@Column(name = "request_quantity")
	private Integer requestQuantity;

	@Column(name = "approved_quantity")
	private Integer approvedQuantity;

}
