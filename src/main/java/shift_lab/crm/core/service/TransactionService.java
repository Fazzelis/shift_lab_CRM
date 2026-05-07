package shift_lab.crm.core.service;

import shift_lab.crm.api.dto.request.transaction.TransactionCreateRequestDto;
import shift_lab.crm.core.entity.TransactionEntity;

import java.util.Optional;

public interface TransactionService {
    Optional<TransactionEntity> create(TransactionCreateRequestDto transactionDto);
}
