package com.project.irs_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.project.irs_backend.dto.MaterialOrderItemRequest;
import com.project.irs_backend.dto.MaterialOrderRequest;
import com.project.irs_backend.dto.MaterialOrderResponseDto;
import com.project.irs_backend.entity.Material;
import com.project.irs_backend.entity.MaterialOrder;
import com.project.irs_backend.entity.MaterialOrderItem;
import com.project.irs_backend.entity.Supplier;
import com.project.irs_backend.entity.SupplierMaterial;
import com.project.irs_backend.enums.OrderStatus;
import com.project.irs_backend.repository.MaterialOrderItemRepository;
import com.project.irs_backend.repository.MaterialOrderRepository;
import com.project.irs_backend.repository.MaterialRepository;
import com.project.irs_backend.repository.SupplierMaterialRepository;
import com.project.irs_backend.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialOrderService {

	private final MaterialOrderRepository materialOrderRepository;

	private final MaterialOrderItemRepository materialOrderItemRepository;

	private final SupplierMaterialRepository supplierMaterialRepository;

	private final SupplierRepository supplierRepository;

	private final MaterialRepository materialRepository;

//	public List<MaterialOrderResponseDto> getAllOrders() {
//		List<Object[]> rows = materialOrderRepository.getAllOrderDetails();
//		List<MaterialOrderResponseDto> result = new ArrayList<>();
//		
//		for(Material row : rows) {
//			
//		}
	
//	}

	public List<MaterialOrder> createOrders(List<MaterialOrderRequest> requests) {
		List<MaterialOrder> orders = new ArrayList<>();

		for (MaterialOrderRequest request : requests) {
			Supplier supplier = supplierRepository.findById(request.getSupplierId())
					.orElseThrow(() -> new RuntimeException("Supplier not found"));

			MaterialOrder order = new MaterialOrder();

			BeanUtils.copyProperties(request, order);

			order.setSupplier(supplier);
			order.setOrderDateTime(LocalDateTime.now());
			order.setOrderStatus(OrderStatus.PENDING);

			MaterialOrder savedOrder = materialOrderRepository.save(order);

			for (MaterialOrderItemRequest itemRequest : request.getItems()) {

				Material material = materialRepository.findById(itemRequest.getMaterialId())
						.orElseThrow(() -> new RuntimeException("Material not found"));

				SupplierMaterial supplierMaterial = supplierMaterialRepository
						.findBySupplier_SupplierIdAndMaterial_MaterialId(request.getSupplierId(),
								itemRequest.getMaterialId())
						.orElseThrow(() -> new RuntimeException("Material not available for this supplier"));

				if (supplierMaterial.getPrice().compareTo(itemRequest.getPrice()) != 0) {

					throw new RuntimeException("Price changed for material: " + material.getMaterialName());
				}

				MaterialOrderItem orderItem = new MaterialOrderItem();

				BeanUtils.copyProperties(itemRequest, orderItem);

				orderItem.setOrder(savedOrder);
				orderItem.setMaterial(material);

				materialOrderItemRepository.save(orderItem);
			}

			orders.add(savedOrder);
		}

		return orders;
	}

}
