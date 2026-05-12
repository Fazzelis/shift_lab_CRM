package shift_lab.crm.api.dto.request.seller;

import lombok.Builder;

@Builder
public record SellerCreateRequestDto(
        String name,
        String contactInfo
) {
}
