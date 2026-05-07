package shift_lab.crm.core.service;

import shift_lab.crm.api.dto.request.seller.SellerCreateRequestDto;
import shift_lab.crm.api.dto.request.seller.SellerPatchRequestDto;
import shift_lab.crm.core.entity.SellerEntity;

import java.util.List;
import java.util.Optional;

public interface SellerService {
    SellerEntity create(SellerCreateRequestDto sellerDto);
    SellerEntity getById(String id);
    List<SellerEntity> getAllSellers(int page, int size);
    List<SellerEntity> getAllDeletedSellers(int page, int size);
    SellerEntity update(String id, SellerPatchRequestDto sellerDto);
    String delete(String id);
}
