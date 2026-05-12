package shift_lab.crm.api.dto.response.seller;

import lombok.Builder;

import java.util.List;

@Builder
public record SellersBelowAmountResponseDto(
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        List<SellerBelowAmountResponseDto> sellers
) {
}
