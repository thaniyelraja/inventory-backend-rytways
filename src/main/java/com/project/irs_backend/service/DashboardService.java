package com.project.irs_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.irs_backend.dto.AdminDashboardSummary;
import com.project.irs_backend.dto.DashboardSummary;
import com.project.irs_backend.dto.RequestStatusCount;
import com.project.irs_backend.dto.SpendingOverviewDto;
import com.project.irs_backend.dto.TopMonthlyDto;
import com.project.irs_backend.entity.InventoryRequest;
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

	public DashboardSummary getDashboardSummary(Long userId, Long departmentId) {

		Long pendingRequests = inventoryRequestRepository.countByStatus("PENDING", userId, departmentId);

		Long approvedRequests = inventoryRequestRepository.countByStatus("APPROVED", userId, departmentId);

		Long rejectedRequests = inventoryRequestRepository.countByStatus("REJECTED", userId, departmentId);

		BigDecimal thisMonthSpending = inventoryRequestRepository.getThisMonthSpending(userId, departmentId);

		return new DashboardSummary(pendingRequests, approvedRequests, rejectedRequests, thisMonthSpending);
	}
//
//	public List<InventoryRequest> getRecentRequests(Long userId, LocalDateTime startDateTime) {
//		return inventoryRequestRepository.findRecentRequests(userId, startDateTime);
//	}
//
//	public List<SpendingOverviewDto> getSpendingOverview(Long userId, LocalDateTime fromMonth, LocalDateTime toMonth) {
//		List<Object[]> results = inventoryRequestRepository.getSpendingOverview(userId, fromMonth, toMonth);
//
//		Map<String, BigDecimal> spendingMap = new HashMap<>();
//		for (Object[] row : results) {
//			Integer month = ((Number) row[0]).intValue();
//			Integer year = ((Number) row[1]).intValue();
//			BigDecimal spending = (BigDecimal) row[2];
//
//			spendingMap.put(year + "-" + month, spending);
//		}
//		List<SpendingOverviewDto> response = new ArrayList<>();
//
//		LocalDate current = fromMonth.toLocalDate().withDayOfMonth(1);
//		LocalDate end = toMonth.toLocalDate().withDayOfMonth(1);
//
//		while (!current.isAfter(end)) {
//			int month = current.getMonthValue();
//			int year = current.getYear();
//
//			BigDecimal spending = spendingMap.getOrDefault(year + "-" + month, BigDecimal.ZERO);
//
//			response.add(new SpendingOverviewDto(month, year, spending));
//			current = current.plusMonths(1);
//		}
//		return response;
//	}
//
//	public List<ProductTrend> getProductTrend(String period, LocalDate startDate, Integer month, Integer year) {
//
//		List<Object[]> result;
//
//		if ("WEEKLY".equalsIgnoreCase(period)) {
//			result = inventoryRequestRepository.getWeeklyProductTrend(startDate);
//		} else if ("MONTHLY".equalsIgnoreCase(period)) {
//			result = inventoryRequestRepository.getMonthlyProductTrend(month, year);
//		} else {
//			result = inventoryRequestRepository.getYearlyProductTrend(year);
//		}
//
//		Map<String, ProductTrend> map = new LinkedHashMap<>();
//
//		for (Object[] row : result) {
//			String periodName = row[0].toString();
//			String productName = row[2] != null ? row[2].toString() : null;
//			Long quantity = Long.parseLong(row[3].toString());
//
//			if (!map.containsKey(periodName)) {
//				map.put(periodName, new ProductTrend(periodName, new LinkedHashMap<>()));
//			}
//			if (productName != null) {
//				map.get(periodName).getProducts().put(productName, quantity);
//			}
//		}
//
//		return new ArrayList<>(map.values());
//	}
//
//	public List<RequestStatusCount> getRequestStatus(Long userId) {
//
//		List<Object[]> result = inventoryRequestRepository.getRequestStatus(userId);
//
//		return result.stream()
//				.map(row -> new RequestStatusCount(row[0].toString(), (Long.parseLong(row[1].toString())))).toList();
//
//	}
//
//	public List<TopMonthlyDto> getTopProductByMonth(Integer month) {
//		return inventoryRequestRepository.findTopProductByMonth(month);
//	}
//
//	public AdminDashboardSummary getAdminDashboardSummary() {
//		Long totalProducts = materialRepository.countTotalProducts();
//
//		Long lowStockProducts = materialRepository.countLowStockProducts();
//
//		Long outOfStockProducts = materialRepository.countOutOfStockProducts();
//
//		Long mostStockProducts = materialRepository.countOverStockedProducts();
//
//		return new AdminDashboardSummary(totalProducts, lowStockProducts, outOfStockProducts, mostStockProducts);
//	}
}