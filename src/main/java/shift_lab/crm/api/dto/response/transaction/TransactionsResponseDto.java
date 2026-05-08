package shift_lab.crm.api.dto.response.transaction;

import lombok.Builder;

import java.util.List;

@Builder
public record TransactionsResponseDto(
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        List<TransactionResponseDto> transactions
) {
}
