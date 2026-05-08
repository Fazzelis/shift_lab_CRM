package shift_lab.crm.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shift_lab.crm.core.entity.SellerEntity;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<SellerEntity,Long> {
    @Query("SELECT s from SellerEntity s WHERE s.isDeleted = true")
    Page<SellerEntity> findByIsDeletedTrue(Pageable pageable);

    @Query("SELECT s from SellerEntity s WHERE s.isDeleted = false AND s.id = :id")
    Optional<SellerEntity> findByIsDeletedFalse(Long id);

    @Query("SELECT s from SellerEntity s WHERE s.isDeleted = false")
    Page<SellerEntity> findAllNotDeleted(Pageable pageable);
}
