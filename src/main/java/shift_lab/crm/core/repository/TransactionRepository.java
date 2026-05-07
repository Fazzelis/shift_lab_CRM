package shift_lab.crm.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shift_lab.crm.core.entity.TransactionEntity;

public interface TransactionRepository extends JpaRepository<TransactionEntity,Long> {
}
