package shift_lab.crm.core.service;

import org.springframework.data.domain.Page;
import shift_lab.crm.api.dto.request.seller.SellerRequestDto;
import shift_lab.crm.core.entity.SellerEntity;

import java.util.List;
import java.util.Optional;

public interface SellerService {
    SellerEntity create(SellerRequestDto sellerDto);
    Optional<SellerEntity> getById(Long id);
    List<SellerEntity> getAllSellers(int page, int size);
}
