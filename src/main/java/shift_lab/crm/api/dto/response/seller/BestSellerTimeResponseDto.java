package shift_lab.crm.api.dto.response.seller;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BestSellerTimeResponseDto(
        LocalDate startBestTime,
        LocalDate endBestTime,
        int transactionCount
) {
}
