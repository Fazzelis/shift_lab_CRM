package shift_lab.crm.api.dto.response.seller;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SellerResponseDto(
        Long id,
        String name,
        String contactInfo,
        LocalDateTime registrationDate
) {
}
