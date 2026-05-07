package shift_lab.crm.core.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shift_lab.crm.api.dto.request.transaction.TransactionCreateRequestDto;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.repository.SellerRepository;
import shift_lab.crm.core.repository.TransactionRepository;
import shift_lab.crm.core.service.TransactionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;
    private SellerRepository sellerRepository;

    @Override
    public Optional<TransactionEntity> create(TransactionCreateRequestDto transactionDto) {
        Optional<SellerEntity> sellerEntity = sellerRepository.findByIsDeletedFalse(transactionDto.sellerId());
        if  (sellerEntity.isPresent()) {
            TransactionEntity newTransactionEntity = TransactionEntity.builder()
                    .seller(sellerEntity.get())
                    .amount(transactionDto.amount())
                    .paymentType(transactionDto.paymentType())
                    .transactionDate(LocalDateTime.now())
                    .build();
            return Optional.of(transactionRepository.save(newTransactionEntity));
        }
        return Optional.empty();
    }

    @Override
    public List<TransactionEntity> findAll(int  page, int size) {
        Pageable  pageable = PageRequest.of(page, size);
        return transactionRepository.findAll(pageable).getContent();
    }

    @Override
    public Optional<TransactionEntity> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public List<TransactionEntity> findAllBySellerId(Long id) {
        return  transactionRepository.findAllBySellerId(id);
    }
}
