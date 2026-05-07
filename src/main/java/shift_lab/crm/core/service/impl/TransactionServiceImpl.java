package shift_lab.crm.core.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shift_lab.crm.api.dto.request.transaction.TransactionCreateRequestDto;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.repository.SellerRepository;
import shift_lab.crm.core.repository.TransactionRepository;
import shift_lab.crm.core.service.TransactionService;

import java.time.LocalDateTime;
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
}
