package shift_lab.crm.unit.mapper;
import org.junit.jupiter.api.Test;
import shift_lab.crm.api.dto.response.seller.SellerResponseDto;
import shift_lab.crm.api.mapper.SellerMapper;
import shift_lab.crm.core.entity.SellerEntity;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

public class SellerMapperTest {
    private final SellerMapper sellerMapper = new SellerMapper();

    @Test
    public void mapTest() {
        SellerEntity sellerEntity = SellerEntity.builder()
                .id(1L)
                .name("Seller")
                .contactInfo("Contact Info")
                .registrationDate(LocalDateTime.now())
                .isDeleted(false)
                .build();
        SellerResponseDto result = sellerMapper.map(sellerEntity);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(SellerResponseDto.builder()
                        .id(1L)
                        .name("Seller")
                        .contactInfo("Contact Info")
                        .registrationDate(sellerEntity.getRegistrationDate())
                .build());
    }
}
