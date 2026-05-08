package shift_lab.crm.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shift_lab.crm.core.entity.TransactionEntity;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {
    @Query("SELECT t FROM TransactionEntity t WHERE t.seller.id = :sellerId")
    Page<TransactionEntity> findAllBySellerId(Pageable pageable, Long sellerId);
}
