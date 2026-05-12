package shift_lab.crm.api.dto.request.seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SellerPatchRequestDto(
        String name,
        String contactInfo
) {
}
