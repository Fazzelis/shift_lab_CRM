package shift_lab.crm.core.service;

import org.springframework.data.domain.Page;
import shift_lab.crm.api.dto.request.transaction.TransactionCreateRequestDto;
import shift_lab.crm.core.entity.TransactionEntity;

public interface TransactionService {
    TransactionEntity create(TransactionCreateRequestDto transactionDto);
    Page<TransactionEntity> findAll(int  page, int size);
    TransactionEntity findById(Long id);
    Page<TransactionEntity> findAllBySellerId(Long id, int page, int size);
}
