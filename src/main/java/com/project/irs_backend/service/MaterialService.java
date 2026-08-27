package com.project.irs_backend.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.irs_backend.dto.CategoryDto;
import com.project.irs_backend.dto.MaterialSaveDto;
import com.project.irs_backend.dto.MaterialDto;
import com.project.irs_backend.entity.Category;
import com.project.irs_backend.entity.Material;
import com.project.irs_backend.repository.CategoryRepository;
import com.project.irs_backend.repository.MaterialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialService {

	private final MaterialRepository materialRepository;

	private final CategoryRepository categoryRepository;

	public List<CategoryDto> getAllCategories() {
		List<Category> categories = materialRepository.findAllCategories();
		List<CategoryDto> result = new ArrayList<>();

		for (Category category : categories) {
			CategoryDto dto = new CategoryDto();
			BeanUtils.copyProperties(category, dto);
			result.add(dto);
		}

		return result;
	}

	public Page<Material> getAllMaterials(String search, String status, Long categoryId,
			Pageable pageable) {

		return materialRepository.searchAndFilter(search, status, categoryId, pageable);

	}

	public void exportMaterialToExcel(OutputStream outputStream) throws IOException {
		List<Material> materials = materialRepository.findAll();
		SXSSFWorkbook workbook = new SXSSFWorkbook(100);
		SXSSFSheet sheet = workbook.createSheet("Materials");
		sheet.trackAllColumnsForAutoSizing();

		Row header = sheet.createRow(0);

		header.createCell(0).setCellValue("S.No");
		header.createCell(1).setCellValue("Material Code");
		header.createCell(2).setCellValue("Material Name");
		header.createCell(3).setCellValue("Available Quantity");
		header.createCell(4).setCellValue("Unit");
		header.createCell(5).setCellValue("Status");

		for (int i = 0; i < materials.size(); i++) {
			Material material = materials.get(i);
			Row row = sheet.createRow(i + 1);

			row.createCell(0).setCellValue(i + 1);
			row.createCell(1).setCellValue(material.getMaterialCode());
			row.createCell(2).setCellValue(material.getMaterialName());
			row.createCell(3).setCellValue(material.getAvailableQuantity());
			row.createCell(4).setCellValue(material.getUnit());

			String status = material.getAvailableQuantity() > 0 ? "Available" : "Out of Stock";

			row.createCell(5).setCellValue(status);

		}

		for (int i = 0; i < 6; i++) {
			sheet.autoSizeColumn(i);
		}

		workbook.write(outputStream);
		workbook.close();

	}

	public Material createMaterial(MaterialSaveDto dto) {
		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found"));
		Material material = new Material();
		BeanUtils.copyProperties(dto, material);
		material.setCategory(category);

		return materialRepository.save(material);
	}

	public Material updateMaterial(Long materialId, MaterialSaveDto dto) {
		Material existingMaterial = materialRepository.findById(materialId)
				.orElseThrow(() -> new RuntimeException("Material not found"));

		BeanUtils.copyProperties(dto, existingMaterial);
		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found"));

		existingMaterial.setCategory(category);
		return materialRepository.save(existingMaterial);
	}

	public void deleteMaterial(Long materialId) {
		Material material = materialRepository.findById(materialId)
				.orElseThrow(() -> new RuntimeException("Material not found"));
		if (material.getSupplierMaterils() != null && !material.getSupplierMaterils().isEmpty()) {
			throw new RuntimeException("You can't directly delete this material because it's linked to supplier");
		}
		if (material.getInventoryRequest() != null && !material.getInventoryRequest().isEmpty()) {
			throw new RuntimeException("Can't delete this material because it have request history");
		}
		materialRepository.delete(material);
	}

	public List<CategoryDto> getCategoriesBySupplierId(Long supplierId) {
		return materialRepository.findCategoriesBySupplierId(supplierId);
	}

	public List<MaterialDto> getMaterialsByCategory(Long supplierId, Long categoryId) {
		return materialRepository.findMaterialsByCategory(supplierId, categoryId);
	}

	public List<MaterialDto> getMaterialsByCategories(Long categoryId) {
		return materialRepository.findMaterialsByCategories(categoryId);
	}

}
