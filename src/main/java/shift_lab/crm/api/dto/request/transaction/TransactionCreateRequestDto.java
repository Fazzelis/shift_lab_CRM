package shift_lab.crm.api.dto.request.transaction;

import shift_lab.crm.core.enums.PaymentEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionCreateRequestDto(
        Long sellerId,
        BigDecimal amount,
        PaymentEnum paymentType
) {
}
