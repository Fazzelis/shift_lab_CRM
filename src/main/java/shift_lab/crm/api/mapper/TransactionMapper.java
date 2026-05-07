package shift_lab.crm.api.mapper;

import lombok.Builder;
import org.springframework.stereotype.Component;
import shift_lab.crm.api.dto.response.transaction.TransactionResponseDto;
import shift_lab.crm.core.entity.TransactionEntity;

@Component
public class TransactionMapper {
    public TransactionResponseDto map(TransactionEntity transactionEntity) {
        return TransactionResponseDto.builder()
                .id(transactionEntity.getId())
                .sellerId(transactionEntity.getSeller().getId())
                .amount(transactionEntity.getAmount())
                .paymentType(transactionEntity.getPaymentType())
                .transactionDate(transactionEntity.getTransactionDate())
                .build();
    }
}
