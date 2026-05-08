package shift_lab.crm.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shift_lab.crm.api.dto.projections.TopSellerProjection;
import shift_lab.crm.core.entity.TransactionEntity;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {
    @Query("SELECT t FROM TransactionEntity t WHERE t.seller.id = :sellerId")
    Page<TransactionEntity> findAllBySellerId(Pageable pageable, Long sellerId);

    @Query("SELECT t.seller as seller, SUM(t.amount) as totalAmount from TransactionEntity t " +
            "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY t.seller " +
            "ORDER BY SUM(t.amount) DESC " +
            "LIMIT 1")
    TopSellerProjection findTopSellerByPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
