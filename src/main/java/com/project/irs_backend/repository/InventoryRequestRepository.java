package com.project.irs_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.irs_backend.entity.InventoryRequest;

public interface InventoryRequestRepository extends JpaRepository<InventoryRequest, Long> {

	@Query(value = """
			SELECT ir.* FROM inventory_request ir JOIN material m ON ir.material_id = m.material_id
			WHERE
				ir.user_id = :userId
				AND
				(
					:search = ''
					OR LOWER(m.material_code) LIKE LOWER(CONCAT('%', :search, '%'))
					OR LOWER(m.material_name) LIKE LOWER(CONCAT('%', :search, '%'))
				 )
				 AND
				 (
				 :status = 'ALL'
				 OR ir.request_status = :status
				 )
			""", countQuery = """
			 SELECT COUNT(*)
			       FROM inventory_request ir
			       JOIN material m ON ir.material_id = m.material_id
			       WHERE
			       ir.user_id = :userId
			       AND
			           (
			               :search = ''
			               OR LOWER(m.material_code) LIKE LOWER(CONCAT('%', :search, '%'))
			               OR LOWER(m.material_name) LIKE LOWER(CONCAT('%', :search, '%'))
			           )
			           AND
			           (
			               :status = 'ALL'
			               OR ir.request_status = :status
			           )
			""", nativeQuery = true)
	Page<InventoryRequest> searchAndFilterPerUser(@Param("search") String search, @Param("status") String status,
			@Param("userId") Long userId, Pageable pageable);

	@Query(value = """
			SELECT ir.* FROM inventory_request ir JOIN material m ON ir.material_id = m.material_id
				AND
				(
					:search = ''
					OR LOWER(m.material_code) LIKE LOWER(CONCAT('%', :search, '%'))
					OR LOWER(m.material_name) LIKE LOWER(CONCAT('%', :search, '%'))
				 )
				 AND
				 request_status = 'PENDING'
			""", countQuery = """
			 SELECT COUNT(*)
			       FROM inventory_request ir
			       JOIN material m ON ir.material_id = m.material_id
			       AND
			           (
			               :search = ''
			               OR LOWER(m.material_code) LIKE LOWER(CONCAT('%', :search, '%'))
			               OR LOWER(m.material_name) LIKE LOWER(CONCAT('%', :search, '%'))
			           )
			           AND ir.request_status = 'PENDING'
			""", nativeQuery = true)
	Page<InventoryRequest> searchAndFilterAllManage(@Param("search") String search, Pageable pageable);

	@Query(value = """
			SELECT ir.* FROM inventory_request ir JOIN material m ON ir.material_id = m.material_id

				AND
				(
					:search = ''
					OR LOWER(m.material_code) LIKE LOWER(CONCAT('%', :search, '%'))
					OR LOWER(m.material_name) LIKE LOWER(CONCAT('%', :search, '%'))
				 )
				 AND
				 request_status IN ('APPROVED', 'REJECTED')
			""", countQuery = """
			 SELECT COUNT(*)
			       FROM inventory_request ir
			       JOIN material m ON ir.material_id = m.id

			       AND
			           (
			               :search = ''
			               OR LOWER(m.material_code) LIKE LOWER(CONCAT('%', :search, '%'))
			               OR LOWER(m.material_name) LIKE LOWER(CONCAT('%', :search, '%'))
			           )
			           AND request_status IN ('APPROVED', 'REJECTED')
			""", nativeQuery = true)
	Page<InventoryRequest> searchAndFilterAllView(@Param("search") String search, Pageable pageable);

	@Query(value = """
			SELECT COUNT(*)
			FROM inventory_request
			WHERE request_status = :status
			AND user_id = :userId
			""", nativeQuery = true)
	Long countByStatus(@Param("status") String status, @Param("userId") Long userId);

	@Query(value = """
			SELECT
			    DAYNAME(ir.requested_at) AS day,

			    SUM(
			        CASE
			            WHEN ir.request_status = 'PENDING' THEN 1
			            ELSE 0
			        END
			    ) AS pending,

			    SUM(
			        CASE
			            WHEN ir.request_status = 'APPROVED' THEN 1
			            ELSE 0
			        END
			    ) AS approved,

			    SUM(
			        CASE
			            WHEN ir.request_status = 'REJECTED' THEN 1
			            ELSE 0
			        END
			    ) AS rejected

			FROM inventory_request ir

			WHERE ir.user_id = :userId

			GROUP BY
			    DAYOFWEEK(ir.requested_at),
			    DAYNAME(ir.requested_at)

			ORDER BY DAYOFWEEK(ir.requested_at)
			""", nativeQuery = true)
	List<Object[]> getRequestTrend(@Param("userId") Long userId);

	@Query(value = """
			SELECT request_status, COUNT(*) FROM inventory_request WHERE user_id = :userId GROUP BY request_status ORDER BY request_status
			""", nativeQuery = true)
	List<Object[]> getRequestStatus(@Param("userId") Long userId);

	List<InventoryRequest> findByUser_UserIdAndMaterial_MaterialId(Long userId, Long materialId);

	@Query(value = """
			SELECT COUNT(*) FROM inventory_request WHERE request_status = :status
			""", nativeQuery = true)
	Long countAllRequestByStatus(@Param("status") String status);

	@Query(value = """
			SELECT
			    DAYNAME(ir.requested_at) AS period,
			    m.material_name,
			    SUM(ir.approved_quantity) AS quantity
			FROM inventory_request ir
			JOIN material m
			    ON ir.material_id = m.id
			JOIN (
			    SELECT material_id
			    FROM inventory_request
			    WHERE requested_at >= :startDate
			      AND requested_at < DATE_ADD(:startDate, INTERVAL 7 DAY)
			      AND approved_quantity IS NOT NULL
			    GROUP BY material_id
			    ORDER BY SUM(approved_quantity) DESC
			    LIMIT 3
			) top3
			    ON ir.material_id = top3.material_id
			WHERE ir.requested_at >= :startDate
			  AND ir.requested_at < DATE_ADD(:startDate, INTERVAL 7 DAY)
			  AND ir.approved_quantity IS NOT NULL
			GROUP BY
			    DAYOFWEEK(ir.requested_at),
			    DAYNAME(ir.requested_at),
			    m.id,
			    m.material_name
			ORDER BY
			    DAYOFWEEK(ir.requested_at)
			""", nativeQuery = true)
	List<Object[]> getWeeklyProductTrend(@Param("startDate") LocalDate startDate);

	@Query(value = """
			SELECT
			    CONCAT(
			        'Week ',
			        FLOOR((DAY(ir.requested_at) - 1) / 7) + 1
			    ) AS period,
			    m.material_name,
			    COALESCE(SUM(ir.approved_quantity), 0) AS quantity
			FROM inventory_request ir
			JOIN material m
			    ON ir.material_id = m.id
			JOIN (
			    SELECT material_id
			    FROM inventory_request
			    WHERE MONTH(requested_at) = :month
			      AND YEAR(requested_at) = :year
			      AND approved_quantity IS NOT NULL
			    GROUP BY material_id
			    ORDER BY SUM(approved_quantity) DESC
			    LIMIT 3
			) top3
			    ON ir.material_id = top3.material_id
			WHERE MONTH(ir.requested_at) = :month
			  AND YEAR(ir.requested_at) = :year
			  AND ir.approved_quantity IS NOT NULL
			GROUP BY
			    FLOOR((DAY(ir.requested_at) - 1) / 7) + 1,
			    m.id,
			    m.material_name
			ORDER BY
			    FLOOR((DAY(ir.requested_at) - 1) / 7) + 1,
			    quantity DESC
			""", nativeQuery = true)
	List<Object[]> getMonthlyProductTrend(@Param("month") Integer month, @Param("year") Integer year);

	@Query(value = """
			SELECT
			    MONTHNAME(ir.requested_at) AS period,
			    m.material_name,
			    SUM(ir.approved_quantity) AS quantity
			FROM inventory_request ir
			JOIN material m
			    ON ir.material_id = m.id
			JOIN (
			    SELECT material_id
			    FROM inventory_request
			    WHERE YEAR(requested_at) = :year
			      AND approved_quantity IS NOT NULL
			    GROUP BY material_id
			    ORDER BY SUM(approved_quantity) DESC
			    LIMIT 3
			) top3
			    ON ir.material_id = top3.material_id
			WHERE YEAR(ir.requested_at) = :year
			  AND ir.approved_quantity IS NOT NULL
			GROUP BY
			    MONTH(ir.requested_at),
			    MONTHNAME(ir.requested_at),
			    m.id,
			    m.material_name
			ORDER BY
			    MONTH(ir.requested_at),
			    quantity DESC
			""", nativeQuery = true)
	List<Object[]> getYearlyProductTrend(@Param("year") Integer year);

}
