package shift_lab.crm.core.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shift_lab.crm.api.dto.projections.SellerBelowAmountProjection;
import shift_lab.crm.api.dto.projections.TopSellerProjection;
import shift_lab.crm.api.dto.request.seller.SellerCreateRequestDto;
import shift_lab.crm.api.dto.request.seller.SellerPatchRequestDto;
import shift_lab.crm.api.dto.response.seller.BestSellerTimeResponseDto;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.enums.ErrorCode;
import shift_lab.crm.core.exception.BusinessException;
import shift_lab.crm.core.repository.SellerRepository;
import shift_lab.crm.core.repository.TransactionRepository;
import shift_lab.crm.core.service.SellerService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SellerServiceImpl implements SellerService {
    private SellerRepository sellerRepository;
    private TransactionRepository transactionRepository;

    @Override
    public SellerEntity create(SellerCreateRequestDto sellerDto)
    {
        SellerEntity sellerEntity = SellerEntity.builder()
                .name(sellerDto.name())
                .contactInfo(sellerDto.contactInfo())
                .registrationDate(LocalDateTime.now())
                .build();
        return sellerRepository.save(sellerEntity);
    }

    @Override
    public SellerEntity getById(String id)
    {
        try {
            Long parsedId = Long.parseLong(id);
            Optional<SellerEntity> optionalSellerEntity = sellerRepository.findById(parsedId);
            if (optionalSellerEntity.isPresent() && !optionalSellerEntity.get().getIsDeleted()) {
                return optionalSellerEntity.get();
            }
            throw new BusinessException(ErrorCode.SELLER_NOT_FOUND);
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.NUMBER_FORMAT_EXCEPTION, "Неверный формат id");
        }
    }

    @Override
    public Page<SellerEntity> getAllSellers(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return sellerRepository.findAllNotDeleted(pageable);
    }

    @Override
    public Page<SellerEntity> getAllDeletedSellers(int page, int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return sellerRepository.findByIsDeletedTrue(pageable);
    }

    @Override
    public SellerEntity update(String id, SellerPatchRequestDto sellerDto)
    {
        SellerEntity sellerEntity = getById(id);
        if (sellerDto.name() != null) {
            sellerEntity.setName(sellerDto.name());
        }
        if (sellerDto.contactInfo() != null) {
            sellerEntity.setContactInfo(sellerDto.contactInfo());
        }
        return sellerRepository.save(sellerEntity);
    }

    @Override
    public String delete(String id)
    {
        SellerEntity sellerEntity = getById(id);
        sellerEntity.setIsDeleted(true);
        sellerRepository.save(sellerEntity);
        return "Продавец успешно удален";
    }

    @Override
    public TopSellerProjection getTopSeller(LocalDateTime startDate, LocalDateTime endDate)
    {
        TopSellerProjection topSellerProjection = transactionRepository.findTopSellerByPeriod(startDate, endDate);
        if (topSellerProjection != null) {
            return topSellerProjection;
        }

        throw new BusinessException(ErrorCode.SELLER_NOT_FOUND, "Самый продуктивный продавец за указанную дату не найден");
    }

    @Override
    public Page<SellerBelowAmountProjection> getSellersBelow(
            BigDecimal amount,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size
    )
    {
        return sellerRepository.findAllBelowAmount(startDate, endDate, amount, PageRequest.of(page, size));
    }

    @Override
    public BestSellerTimeResponseDto getBestSellerTime(String sellerId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Long parsedId = Long.parseLong(sellerId);
            List<TransactionEntity> transactions = transactionRepository.findSellerTransactionByPeriod(parsedId, startDate, endDate);
            if (transactions.isEmpty()) {
                throw new BusinessException(ErrorCode.EMPTY_LIST, "Транзакции не найдены");
            }
            int i = 1;
            List<TransactionEntity> tempList = new ArrayList<>();
            tempList.add(transactions.getFirst());

            List<TransactionEntity> result = new ArrayList<>();
            while (i < transactions.size()) {
                LocalDate currentDate = transactions.get(i).getTransactionDate().toLocalDate();
                LocalDate localDateBefore = transactions.get(i - 1).getTransactionDate().toLocalDate();
                if (currentDate.isEqual(localDateBefore) ||  currentDate.isEqual(localDateBefore.plusDays(1))) {
                    tempList.add(transactions.get(i));
                    i++;
                    continue;
                }
                if (tempList.size() > result.size()) {
                    result = List.copyOf(tempList);
                }
                tempList.clear();
                tempList.add(transactions.get(i));
                i++;
            }
            if (tempList.size() > result.size()) {
                result = List.copyOf(tempList);
            }

            return BestSellerTimeResponseDto.builder()
                    .transactionCount(result.size())
                    .startBestTime(result.getFirst().getTransactionDate().toLocalDate())
                    .endBestTime(result.getLast().getTransactionDate().toLocalDate())
                    .build();
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.NUMBER_FORMAT_EXCEPTION);
        }

    }
}
