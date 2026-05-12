package shift_lab.crm.api.dto.response.seller;

import lombok.Builder;

import java.util.List;

@Builder
public record SellersResponseDto(
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        List<SellerResponseDto> sellers
) {
}
