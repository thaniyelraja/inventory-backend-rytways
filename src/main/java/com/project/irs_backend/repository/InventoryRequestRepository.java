package com.project.irs_backend.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.irs_backend.entity.InventoryRequest;

public interface InventoryRequestRepository extends JpaRepository<InventoryRequest, Long> {

	@Query(value = """
			SELECT distinct ir.* from inventory_request ir join inventory_request_item iri
			ON ir.inventory_request_id = iri.inventory_request_id
			join inventory i on iri.inventory_id = i.inventory_id
			join material m on i.material_id = m.material_id
			join unit u on m.unit_id = u.unit_id
			join status s on ir.status_id = s.status_id
			where ir.user_id = :userId
			and ir.department_id = :departmentId
			and (
				:search = ''
				or lower(m.material_code) like lower(concat('%', :search, '%'))
				or lower(m.material_name) like lower(concat('%', :search, '%'))
				)
			and (
				:status = 'ALL'
				or s.status_code = :status
				)
				and (
				:fromDate is null
				or ir.requested_at >= :fromDate
				)
			and (
				:toDate is null
				or ir.requested_at < date_add(:toDate, interval 1 day)
				)
			order by ir.requested_at desc;
						""", countQuery = """
			SELECT count(distinct ir.inventory_request_id) from inventory_request ir join inventory_request_item iri
			ON ir.inventory_request_id = iri.inventory_request_id
			join inventory i on iri.inventory_id = i.inventory_id
			join material m on i.material_id = m.material_id
			join unit u on m.unit_id = u.unit_id
			join status s on ir.status_id = s.status_id
			where ir.user_id = :userId
			and ir.department_id = :department_id
			and (
				:search = ''
				or lower(m.material_code) like lower(concat('%', :search, '%'))
				or lower(m.material_name) like lower(concat('%', :search, '%'))
				)
			and (
				:status = 'ALL'
				or s.status_code = :status
				)
			and (
				:fromDate is null
				or ir.requested_at >= :fromDate
				)
			and (
				:toDate is null
				or ir.requested_at < date_add(:toDate, interval 1 day)
				)
			order by ir.requested_at desc;
						""", nativeQuery = true)
	Page<InventoryRequest> searchAndFilterPerUser(@Param("search") String search, @Param("status") String status,
			@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate,
			@Param("departmentId") Long departmentId, @Param("userId") Long userId, Pageable pageable);

	@Query(value = """
			SELECT DISTINCT ir.* FROM inventory_request ir join users u on ir.user_id = u.user_id
			join inventory_request_item iri on ir.inventory_request_id = iri.inventory_request_id
			JOIN inventory i ON iri.inventory_id = i.inventory_id
			join material m on i.material_id = m.material_id
			join status s on ir.status_id = s.status_id
			where ir.department_id = :departmentId
			and s.status_code = 'PENDING'
				AND (
				:search = ''
				or lower(m.material_code) like lower(concat('%', :search, '%'))
				or lower(m.material_name) like lower(concat('%', :search, '%'))
				)
				 ORDER BY ir.requested_at DESC;
						""", countQuery = """
			SELECT count(DISTINCT ir.inventory_request_id) FROM inventory_request ir join users u on ir.user_id = u.user_id
			join inventory_request_item iri on ir.inventory_request_id = iri.inventory_request_id
			JOIN inventory i ON iri.inventory_id = i.inventory_id
			join material m on i.material_id = m.material_id
			join status s on ir.status_id = s.status_id
			where ir.department_id = :departmentId
			AND s.status_code = 'PENDING'
			""", nativeQuery = true)
	Page<InventoryRequest> searchAndFilterAllManage(@Param("departmentId") Long departmentId,
			@Param("search") String search, Pageable pageable);

	List<InventoryRequest> findByDepartment_DepartmentIdAndRequestedAtGreaterThanEqualAndRequestedAtLessThan(
			Long departmentId, LocalDateTime startDay, LocalDateTime endOfDay);

	@Query(value = """
			SELECT ir.*
			FROM inventory_request ir
			JOIN users u
			    ON ir.user_id = u.user_id
			JOIN status s
			    ON ir.status_id = s.status_id
			WHERE ir.department_id = :departmentId
			  AND s.status_code IN ('APPROVED', 'REJECTED')
			  AND (
			        :search = ''
			        OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))
			        OR EXISTS (
			            SELECT 1
			            FROM inventory_request_item iri
			            JOIN inventory i
			                ON iri.inventory_id = i.inventory_id
			            JOIN material m
			                ON i.material_id = m.material_id
			            WHERE iri.inventory_request_id = ir.inventory_request_id
			              AND (
			                    LOWER(m.material_code) LIKE LOWER(CONCAT('%', :search, '%'))
			                    OR LOWER(m.material_name) LIKE LOWER(CONCAT('%', :search, '%'))
			                  )
			        )
			      )
			ORDER BY
			    CASE
			        WHEN s.status_code = 'APPROVED' THEN ir.approved_at
			        WHEN s.status_code = 'REJECTED' THEN ir.rejected_at
			    END DESC
			""", countQuery = """
			SELECT COUNT(*)
			FROM inventory_request ir
			JOIN status s
			    ON ir.status_id = s.status_id
			WHERE ir.department_id = :departmentId
			  AND s.status_code IN ('APPROVED', 'REJECTED')
			  AND (
			        :search = ''
			        OR EXISTS (
			            SELECT 1
			            FROM inventory_request_item iri
			            JOIN inventory i
			                ON iri.inventory_id = i.inventory_id
			            JOIN material m
			                ON i.material_id = m.material_id
			            WHERE iri.inventory_request_id = ir.inventory_request_id
			              AND (
			                    LOWER(m.material_code) LIKE LOWER(CONCAT('%', :search, '%'))
			                    OR LOWER(m.material_name) LIKE LOWER(CONCAT('%', :search, '%'))
			                  )
			        )
			      )
			""", nativeQuery = true)
	Page<InventoryRequest> searchAndFilterAllView(@Param("departmentId") Long departmentId,
			@Param("search") String search, Pageable pageable);

	List<InventoryRequest> findByStatus_StatusCodeAndRequestedAtBeforeAndReminderSentFalse(String statusCose,
			LocalDateTime requestedAt);

	@Query(value = """
			SELECT COUNT(*) FROM inventory_request ir join status s on ir.status_id = s.status_id
			WHERE s.status_code = :status
				AND ir.user_id = :userId and department_id = :departmentId;
			""", nativeQuery = true)
	Long countByStatus(@Param("status") String status, @Param("userId") Long userId,
			@Param("departmentId") Long departmentId);

	@Query(value = """
			 select coalesce(sum(iri.approved_quantity * m.material_price), 0) from inventory_request ir join
			 inventory_request_item iri on ir.inventory_request_id = iri.inventory_request_id
			 join inventory i on iri.inventory_id = i.inventory_id
			 join material m on i.material_id = m.material_id
			         join status s on ir.status_id = s.status_id
			         join users u on ir.user_id = u.user_id
			         join department d on ir.department_id = d.department_id
			         where d.department_id = :departmentId and u.user_id = :userId
			and s.status_code = 'APPROVED' and month(ir.approved_at) = MONTH(CURRENT_DATE)
			and year(ir.approved_at) = year(CURRENT_DATE);
						""", nativeQuery = true)
	BigDecimal getThisMonthSpending(@Param("userId") Long userId, @Param("departmentId") Long departmentId);

//	@Query(value = """
//			SELECT ir.* FROM inventory_request ir where ir.user_id = :userId and
//			(:startDateTime IS NULL or(
//			date(ir.requested_at) = DATE(:startDateTime) and
//			time(ir.requested_at) >= TIME(:startDateTime) ))order by ir.requested_at desc limit 5;
//						""", nativeQuery = true)
//	List<InventoryRequest> findRecentRequests(@Param("userId") Long userId,
//			@Param("startDateTime") LocalDateTime startDateTime);
//
//	@Query(value = """
//			select month(ir.approved_at) as month, year(ir.approved_at) as year,
//			sum(ir.approved_quantity * m.material_price) as spending from inventory_request ir
//			join material m on m.material_id = ir.material_id
//			where ir.user_id = :userId
//			and ir.request_status = 'APPROVED' AND ir.approved_at >= :fromMonth
//			and ir.approved_at < date_add(:toMonth, INTERVAL 1 MONTH) group by year(ir.approved_at), month(ir.approved_at)
//			order by year(ir.approved_at), month(ir.approved_at);
//						""", nativeQuery = true)
//	List<Object[]> getSpendingOverview(@Param("userId") Long userId, @Param("fromMonth") LocalDateTime fromMonth,
//			@Param("toMonth") LocalDateTime toMonth);
//
//	@Query(value = """
//			SELECT
//			    DAYNAME(ir.requested_at) AS day,
//
//			    SUM(
//			        CASE
//			            WHEN ir.request_status = 'PENDING' THEN 1
//			            ELSE 0
//			        END
//			    ) AS pending,
//
//			    SUM(
//			        CASE
//			            WHEN ir.request_status = 'APPROVED' THEN 1
//			            ELSE 0
//			        END
//			    ) AS approved,
//
//			    SUM(
//			        CASE
//			            WHEN ir.request_status = 'REJECTED' THEN 1
//			            ELSE 0
//			        END
//			    ) AS rejected
//
//			FROM inventory_request ir
//
//			WHERE ir.user_id = :userId
//
//			GROUP BY
//			    DAYOFWEEK(ir.requested_at),
//			    DAYNAME(ir.requested_at)
//
//			ORDER BY DAYOFWEEK(ir.requested_at)
//			""", nativeQuery = true)
//	List<Object[]> getRequestTrend(@Param("userId") Long userId);
//
//	@Query(value = """
//			SELECT request_status, COUNT(*) FROM inventory_request WHERE user_id = :userId GROUP BY request_status ORDER BY request_status
//			""", nativeQuery = true)
//	List<Object[]> getRequestStatus(@Param("userId") Long userId);
//
//	@Query(value = """
//			select m.material_name, sum(ir.request_quantity) as total_quantity
//			from inventory_request ir join material m on m.material_id = ir.material_id
//			where month(ir.requested_at) = :month and ir.request_status = 'APPROVED' group by m.material_id, m.material_name
//			order by total_quantity desc limit 5;
//						""", nativeQuery = true)
//	List<TopMonthlyDto> findTopProductByMonth(@Param("month") Integer month);
//
//	List<InventoryRequest> findByUser_UserIdAndMaterial_MaterialId(Long userId, Long materialId);
//
//	@Query(value = """
//			SELECT COUNT(*) FROM inventory_request WHERE request_status = :status
//			""", nativeQuery = true)
//	Long countAllRequestByStatus(@Param("status") String status);
//
//	@Query(value = """
//			SELECT
//			    d.period,
//			    d.request_date,
//			    t.material_name,
//			    COALESCE(t.quantity, 0) AS quantity
//			FROM (
//			    SELECT
//			        DATE_ADD(:startDate, INTERVAL n DAY) AS request_date,
//			        DAYNAME(DATE_ADD(:startDate, INTERVAL n DAY)) AS period
//			    FROM (
//			        SELECT 0 AS n
//			        UNION ALL SELECT 1
//			        UNION ALL SELECT 2
//			        UNION ALL SELECT 3
//			        UNION ALL SELECT 4
//			        UNION ALL SELECT 5
//			        UNION ALL SELECT 6
//			    ) days
//			) d
//			LEFT JOIN (
//			    SELECT
//			        DATE(t.requested_at) AS request_date,
//			        m.material_name,
//			        SUM(t.approved_quantity) AS quantity
//			    FROM inventory_request t
//			    JOIN material m
//			        ON t.material_id = m.material_id
//			    WHERE t.requested_at >= :startDate
//			      AND t.requested_at < DATE_ADD(:startDate, INTERVAL 7 DAY)
//			      AND t.approved_quantity IS NOT NULL
//			    GROUP BY
//			        DATE(t.requested_at),
//			        t.material_id,
//			        m.material_name
//			) t
//			    ON d.request_date = t.request_date
//			LEFT JOIN (
//			    SELECT
//			        request_date,
//			        MAX(quantity) AS max_quantity
//			    FROM (
//			        SELECT
//			            DATE(t.requested_at) AS request_date,
//			            t.material_id,
//			            SUM(t.approved_quantity) AS quantity
//			        FROM inventory_request t
//			        WHERE t.requested_at >= :startDate
//			          AND t.requested_at < DATE_ADD(:startDate, INTERVAL 7 DAY)
//			          AND t.approved_quantity IS NOT NULL
//			        GROUP BY
//			            DATE(t.requested_at),
//			            t.material_id
//			    ) x
//			    GROUP BY request_date
//			) top
//			    ON t.request_date = top.request_date
//			   AND t.quantity = top.max_quantity
//			WHERE t.request_date IS NULL
//			   OR t.quantity = top.max_quantity
//			ORDER BY d.request_date
//			""", nativeQuery = true)
//	List<Object[]> getWeeklyProductTrend(@Param("startDate") LocalDate startDate);
//
//	@Query(value = """
//			SELECT
//			    CONCAT(
//			        'Week ',
//			        FLOOR((DAY(ir.requested_at) - 1) / 7) + 1
//			    ) AS period,
//			    m.material_name,
//			    COALESCE(SUM(ir.approved_quantity), 0) AS quantity
//			FROM inventory_request ir
//			JOIN material m
//			    ON ir.material_id = m.material_id
//			JOIN (
//			    SELECT material_id
//			    FROM inventory_request
//			    WHERE MONTH(requested_at) = :month
//			      AND YEAR(requested_at) = :year
//			      AND approved_quantity IS NOT NULL
//			    GROUP BY material_id
//			    ORDER BY SUM(approved_quantity) DESC
//			    LIMIT 3
//			) top3
//			    ON ir.material_id = top3.material_id
//			WHERE MONTH(ir.requested_at) = :month
//			  AND YEAR(ir.requested_at) = :year
//			  AND ir.approved_quantity IS NOT NULL
//			GROUP BY
//			    FLOOR((DAY(ir.requested_at) - 1) / 7) + 1,
//			    m.material_id,
//			    m.material_name
//			ORDER BY
//			    FLOOR((DAY(ir.requested_at) - 1) / 7) + 1,
//			    quantity DESC
//			""", nativeQuery = true)
//	List<Object[]> getMonthlyProductTrend(@Param("month") Integer month, @Param("year") Integer year);
//
//	@Query(value = """
//			SELECT
//			    MONTHNAME(ir.requested_at) AS period,
//			    m.material_name,
//			    SUM(ir.approved_quantity) AS quantity
//			FROM inventory_request ir
//			JOIN material m
//			    ON ir.material_id = m.id
//			JOIN (
//			    SELECT material_id
//			    FROM inventory_request
//			    WHERE YEAR(requested_at) = :year
//			      AND approved_quantity IS NOT NULL
//			    GROUP BY material_id
//			    ORDER BY SUM(approved_quantity) DESC
//			    LIMIT 3
//			) top3
//			    ON ir.material_id = top3.material_id
//			WHERE YEAR(ir.requested_at) = :year
//			  AND ir.approved_quantity IS NOT NULL
//			GROUP BY
//			    MONTH(ir.requested_at),
//			    MONTHNAME(ir.requested_at),
//			    m.id,
//			    m.material_name
//			ORDER BY
//			    MONTH(ir.requested_at),
//			    quantity DESC
//			""", nativeQuery = true)
//	List<Object[]> getYearlyProductTrend(@Param("year") Integer year);
//
}
