package com.project.irs_backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.irs_backend.dto.CategoryDto;
import com.project.irs_backend.dto.MaterialDto;
import com.project.irs_backend.entity.Category;
import com.project.irs_backend.entity.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {

	@Query(value = """
			SELECT m.material_id, m.material_code, m.material_name, m.material_price FROM material m
			JOIN category c WHERE c.category_id = :categoryId;
						""", nativeQuery = true)
	List<MaterialDto> findMaterialsByCategories(@Param("categoryId") Long categoryId);

	@Query(value = """
			SELECT DISTINCT c.* from category c join material m on m.category_id = c.category_id
			""", nativeQuery = true)
	List<Category> findAllCategories();

	@Query(value = """
			SELECT DISTINCT c.category_id, c.category_name FROM category c JOIN material m ON m.category_id = c.category_id
			JOIN supplier_material sm ON sm.material_id = m.id WHERE sm.supplier_id = :supplierId;
			""", nativeQuery = true)
	List<CategoryDto> findCategoriesBySupplierId(@Param("supplierId") Long supplierId);

	@Query(value = """
						select m.id, m.material_code, m.material_name, sm.price from material m join supplier_material sm on
			sm.material_id = m.id where sm.supplier_id = :supplierId and m.category_id = :categoryId;
						""", nativeQuery = true)
	List<MaterialDto> findMaterialsByCategory(@Param("supplierId") Long supplierId,
			@Param("categoryId") Long categoryId);

	@Query(value = """
			SELECT COUNT(*) from material
			""", nativeQuery = true)
	Long countTotalProducts();

	@Query(value = """
			SELECT COUNT(*) FROM material WHERE available_quantity > 0 AND available_quantity <= reorder_level
			""", nativeQuery = true)
	Long countLowStockProducts();

	@Query(value = """
			SELECT COUNT(*) FROM material WHERE available_quantity = 0
			""", nativeQuery = true)
	Long countOutOfStockProducts();

	@Query(value = """
			SELECT COUNT(*) FROM material WHERE available_quantity > max_stock_level
			""", nativeQuery = true)
	Long countOverStockedProducts();

	@Query(value = """
			SELECT * FROM material WHERE (
				:search = ''
				OR LOWER(material_code) LIKE LOWER(CONCAT('%', :search, '%'))
				OR LOWER(material_name) LIKE LOWER(CONCAT('%', :search, '%'))
				)
				AND
				(
				:status = 'ALL'
				OR (:status = 'AVAILABLE' AND available_quantity > 0)
				OR (:status = 'OUT_OF_STOCK' AND available_quantity = 0)
				)
				AND (
				:categoryId IS NULL
				OR category_id = :categoryId
				)

			""", countQuery = """
			SELECT COUNT(*) FROM material WHERE
				(
				:search = ''
				OR LOWER(material_code) LIKE LOWER(CONCAT('%', :search, '%'))
				OR LOWER(material_name) LIKE LOWER(CONCAT('%', :search, '%'))
				)
				AND
				(
				:status = 'ALL'
				OR (:status = 'AVAILABLE' AND available_quantity > 0)
				OR (:status = 'OUT_OF_STOCK' AND available_quantity = 0)
				)
				AND (
				:categoryId IS NULL
				OR category_id = :categoryId
				)

			""", nativeQuery = true)
	Page<Material> searchAndFilter(@Param("search") String search, @Param("status") String status,
			@Param("categoryId") Long categoryId, Pageable pageable);
}
