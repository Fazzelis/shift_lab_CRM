package shift_lab.crm.api.dto.response.seller;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record SellerBelowAmountResponseDto(
        Long id,
        String name,
        String contactInfo,
        LocalDateTime registrationDate,
        BigDecimal amount
) {
}
