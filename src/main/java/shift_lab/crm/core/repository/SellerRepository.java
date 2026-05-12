package shift_lab.crm.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shift_lab.crm.api.dto.projections.SellerBelowAmountProjection;
import shift_lab.crm.core.entity.SellerEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<SellerEntity,Long> {
    @Query("SELECT s from SellerEntity s WHERE s.isDeleted = true")
    Page<SellerEntity> findByIsDeletedTrue(Pageable pageable);

    @Query("SELECT s from SellerEntity s WHERE s.isDeleted = false AND s.id = :id")
    Optional<SellerEntity> findByIsDeletedFalse(Long id);

    @Query("SELECT s from SellerEntity s WHERE s.isDeleted = false")
    Page<SellerEntity> findAllNotDeleted(Pageable pageable);

    @Query("SELECT s as seller, COALESCE(SUM(t.amount), 0) as totalAmount from SellerEntity s " +
            "LEFT JOIN TransactionEntity t ON s.id = t.seller.id AND t.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s " +
            "HAVING COALESCE(SUM(t.amount), 0) <= :amount " +
            "ORDER BY COALESCE(SUM(t.amount), 0) ASC")
    Page<SellerBelowAmountProjection>  findAllBelowAmount(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("amount") BigDecimal amount,
            Pageable pageable
    );
}
