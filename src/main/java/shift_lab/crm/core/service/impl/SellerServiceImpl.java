package shift_lab.crm.core.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shift_lab.crm.api.dto.request.seller.SellerRequestDto;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.repository.SellerRepository;
import shift_lab.crm.core.service.SellerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SellerServiceImpl implements SellerService {
    private SellerRepository sellerRepository;

    @Override
    public SellerEntity create(SellerRequestDto sellerDto)
    {
        SellerEntity sellerEntity = SellerEntity.builder()
                .name(sellerDto.name())
                .contactInfo(sellerDto.contactInfo())
                .registrationDate(LocalDateTime.now())
                .build();
        sellerRepository.save(sellerEntity);
        return sellerEntity;
    }

    @Override
    public Optional<SellerEntity> getById(Long id)
    {
        return sellerRepository.findById(id);
    }

    @Override
    public List<SellerEntity> getAllSellers(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        Page<SellerEntity> pageResult = sellerRepository.findAll(pageable);
        return pageResult.getContent();
    }
}
