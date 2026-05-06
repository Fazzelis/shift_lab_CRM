package shift_lab.crm.core.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shift_lab.crm.api.dto.request.seller.SellerCreateRequestDto;
import shift_lab.crm.api.dto.request.seller.SellerPatchRequestDto;
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
    public SellerEntity create(SellerCreateRequestDto sellerDto)
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
    public Optional<SellerEntity> getById(String id)
    {
        try {
            Long parsedId = Long.parseLong(id);
            Optional<SellerEntity> optionalSellerEntity = sellerRepository.findById(parsedId);
            if (optionalSellerEntity.isPresent() && !optionalSellerEntity.get().getIsDeleted()) {
                return optionalSellerEntity;
            }
            return Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<SellerEntity> getAllSellers(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return sellerRepository.findAll(pageable).getContent().stream()
                .filter(seller -> !seller.getIsDeleted())
                .toList();
    }

    @Override
    public List<SellerEntity> getAllDeletedSellers(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return sellerRepository.findByIsDeletedTrue(pageable).getContent();
    }

    @Override
    public Optional<SellerEntity> update(String id, SellerPatchRequestDto sellerDto)
    {
        Optional<SellerEntity> optionalSellerEntity = getById(id);
        if (optionalSellerEntity.isPresent()) {
            SellerEntity sellerEntity = optionalSellerEntity.get();
            if (sellerDto.name() != null) {
                sellerEntity.setName(sellerDto.name());
            }
            if (sellerDto.contactInfo() != null) {
                sellerEntity.setContactInfo(sellerDto.contactInfo());
            }
            sellerRepository.save(sellerEntity);
            return Optional.of(sellerEntity);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(String id)
    {
        Optional<SellerEntity> optionalSellerEntity = getById(id);
        if (optionalSellerEntity.isPresent()) {
            SellerEntity sellerEntity = optionalSellerEntity.get();
            sellerEntity.setIsDeleted(true);
            sellerRepository.save(sellerEntity);
            return true;
        }
        return false;
    }
}
