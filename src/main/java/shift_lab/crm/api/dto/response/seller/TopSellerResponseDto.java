package shift_lab.crm.api.dto.response.seller;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TopSellerResponseDto(
        SellerResponseDto seller,
        BigDecimal totalAmount
) {
}
