package com.project.irs_backend.entity;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "material")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Material {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "material_id")
	private Long materialId;

	@Column(name = "material_code", unique = true)
	private String materialCode;

	@Column(name = "material_name")
	private String materialName;

	@Column(name = "material_desc")
	private String materialDesc;

	@ManyToOne
	@JoinColumn(name = "unit_id")
	private Unit unit;

	@Column(name = "material_price")
	private BigDecimal materialPrice;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

}
