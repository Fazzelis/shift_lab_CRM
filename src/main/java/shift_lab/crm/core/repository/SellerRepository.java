package shift_lab.crm.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shift_lab.crm.core.entity.SellerEntity;

public interface SellerRepository extends JpaRepository<SellerEntity,Long> {
}
