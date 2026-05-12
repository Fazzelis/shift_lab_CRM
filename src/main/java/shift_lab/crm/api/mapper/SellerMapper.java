package shift_lab.crm.api.mapper;

import org.springframework.stereotype.Component;
import shift_lab.crm.api.dto.response.seller.SellerResponseDto;
import shift_lab.crm.core.entity.SellerEntity;

@Component
public class SellerMapper {
    public SellerResponseDto map(SellerEntity sellerEntity) {
        return SellerResponseDto.builder()
                .id(sellerEntity.getId())
                .name(sellerEntity.getName())
                .contactInfo(sellerEntity.getContactInfo())
                .registrationDate(sellerEntity.getRegistrationDate())
                .build();
    }
}
