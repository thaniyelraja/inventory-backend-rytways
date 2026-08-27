package com.project.irs_backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.irs_backend.dto.AdminDashboardSummary;
import com.project.irs_backend.dto.DashboardSummary;
import com.project.irs_backend.dto.RequestStatusCount;
import com.project.irs_backend.dto.ProductTrend;
import com.project.irs_backend.repository.InventoryRequestRepository;
import com.project.irs_backend.repository.MaterialRepository;
import com.project.irs_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final MaterialRepository materialRepository;

	private final InventoryRequestRepository inventoryRequestRepository;

	private final UserRepository userRepository;

	public DashboardSummary getDashboardSummary(Long userId) {

		Long totalMaterials = materialRepository.countTotalProducts();

		Long pendingRequests = inventoryRequestRepository.countByStatus("PENDING", userId);

		Long approvedRequests = inventoryRequestRepository.countByStatus("APPROVED", userId);

		Long rejectedRequests = inventoryRequestRepository.countByStatus("REJECTED", userId);

		return new DashboardSummary(totalMaterials, pendingRequests, approvedRequests, rejectedRequests);
	}

	public List<ProductTrend> getProductTrend(String period, LocalDate startDate, Integer month, Integer year) {

		List<Object[]> result;
		
		if("WEEKLY".equalsIgnoreCase(period)) {
			result = inventoryRequestRepository.getWeeklyProductTrend(startDate);
		} else if("MONTHLY".equalsIgnoreCase(period)) {
			result = inventoryRequestRepository.getMonthlyProductTrend(month, year);
		} else {
			result = inventoryRequestRepository.getYearlyProductTrend(year);
		}
		
		Map<String, ProductTrend> map = new LinkedHashMap<>();
		
		for(Object[] row : result) {
			String periodName = row[0].toString();
			String productName = row[1].toString();
			Long quantity = Long.parseLong(row[2].toString());
			
			if(!map.containsKey(periodName)) {
				map.put(periodName, new ProductTrend(periodName, new LinkedHashMap<>()));
			}
			map.get(periodName)
			.getProducts()
			.put(productName, quantity);
		}
		
		return new ArrayList<>(map.values());
	}

	public List<RequestStatusCount> getRequestStatus(Long userId) {

		List<Object[]> result = inventoryRequestRepository.getRequestStatus(userId);

		return result.stream()
				.map(row -> new RequestStatusCount(row[0].toString(), (Long.parseLong(row[1].toString())))).toList();

	}

	public AdminDashboardSummary getAdminDashboardSummary() {
		Long totalProducts = materialRepository.countTotalProducts();

		Long lowStockProducts = materialRepository.countLowStockProducts();

		Long outOfStockProducts = materialRepository.countOutOfStockProducts();

		Long mostStockProducts = materialRepository.countOverStockedProducts();

		return new AdminDashboardSummary(totalProducts, lowStockProducts, outOfStockProducts, mostStockProducts);
	}
}