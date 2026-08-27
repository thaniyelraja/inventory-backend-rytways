package com.project.irs_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.irs_backend.entity.Supplier;
import com.project.irs_backend.service.SupplierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
public class SupplierController {

	private final SupplierService supplierService;

	@PostMapping
	public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
		return ResponseEntity.ok(supplierService.createSupplier(supplier));
	}
	
	@GetMapping("/suppliers")
	public ResponseEntity<List<Supplier>> getAllSuppliers(){
		return ResponseEntity.ok(supplierService.getAllSuppliers());
	}

}
