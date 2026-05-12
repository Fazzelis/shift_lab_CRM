package shift_lab.crm.api.dto.response.transaction;

import lombok.Builder;
import shift_lab.crm.core.enums.PaymentEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionResponseDto(
        Long id,
        Long sellerId,
        BigDecimal amount,
        PaymentEnum paymentType,
        LocalDateTime transactionDate
) {
}
