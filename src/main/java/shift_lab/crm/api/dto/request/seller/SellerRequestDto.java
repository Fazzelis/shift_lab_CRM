package shift_lab.crm.api.dto.request.seller;

import lombok.Builder;

@Builder
public record SellerRequestDto(
        String name,
        String contactInfo
) {
}
