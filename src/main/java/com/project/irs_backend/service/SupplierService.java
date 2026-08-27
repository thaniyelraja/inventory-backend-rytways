package com.project.irs_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.irs_backend.entity.Supplier;
import com.project.irs_backend.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {
	
	private final SupplierRepository supplierRepository;
	
	public Supplier createSupplier(Supplier supplier) {
		return supplierRepository.save(supplier);
	}
	
	public List<Supplier> getAllSuppliers(){
		return supplierRepository.findAll();
	}

}
