package shift_lab.crm.api.dto.request.transaction;

import lombok.Builder;
import shift_lab.crm.core.enums.PaymentEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionCreateRequestDto(
        Long sellerId,
        BigDecimal amount,
        PaymentEnum paymentType
) {
}
