package shift_lab.crm.core.service;

import org.springframework.data.domain.Page;
import shift_lab.crm.api.dto.request.seller.SellerCreateRequestDto;
import shift_lab.crm.api.dto.request.seller.SellerPatchRequestDto;
import shift_lab.crm.core.entity.SellerEntity;

public interface SellerService {
    SellerEntity create(SellerCreateRequestDto sellerDto);
    SellerEntity getById(String id);
    Page<SellerEntity> getAllSellers(int page, int size);
    Page<SellerEntity> getAllDeletedSellers(int page, int size);
    SellerEntity update(String id, SellerPatchRequestDto sellerDto);
    String delete(String id);
}
