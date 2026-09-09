//package com.project.irs_backend.controller;
//
//import java.util.List;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.project.irs_backend.dto.MaterialOrderRequest;
//import com.project.irs_backend.dto.MaterialOrderResponseDto;
//import com.project.irs_backend.entity.MaterialOrder;
//import com.project.irs_backend.service.MaterialOrderService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/orders")
//@RequiredArgsConstructor
//public class MaterialOrderController {
//
//	private final MaterialOrderService materialOrderService;
//
//	@PostMapping("/order")
//	public ResponseEntity<?> createOrders(@RequestBody List<MaterialOrderRequest> requests) {
//		return ResponseEntity.ok(materialOrderService.createOrders(requests));
//	}
//
////	@GetMapping("/all-orders")
////	public ResponseEntity<List<MaterialOrderResponseDto>> getAllOrders() {
////		return ResponseEntity.ok(materialOrderService.getAllOrders());
////	}
//
//}
