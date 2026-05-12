package shift_lab.crm.api.dto.response;

import lombok.Builder;

@Builder
public record BasicResponseDto(
        String message
) {
}
