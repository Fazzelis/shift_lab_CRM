package shift_lab.crm.mapper;

import org.junit.jupiter.api.Test;
import shift_lab.crm.api.dto.response.transaction.TransactionResponseDto;
import shift_lab.crm.api.mapper.TransactionMapper;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.enums.PaymentEnum;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionMapperTest {

    private final TransactionMapper transactionMapper = new TransactionMapper();

    @Test
    public void mapTest() {
        TransactionEntity transactionEntity = TransactionEntity.builder()
                .id(1L)
                .seller(SellerEntity.builder()
                        .id(1L)
                        .name("Seller")
                        .contactInfo("Contact Info")
                        .registrationDate(LocalDateTime.now())
                        .isDeleted(false)
                        .build())
                .amount(BigDecimal.valueOf(5432))
                .paymentType(PaymentEnum.CARD)
                .build();

        TransactionResponseDto result = transactionMapper.map(transactionEntity);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(TransactionResponseDto.builder()
                        .id(1L)
                        .sellerId(1L)
                        .amount(BigDecimal.valueOf(5432))
                        .paymentType(PaymentEnum.CARD)
                        .transactionDate(transactionEntity.getTransactionDate())
                .build());
    }
}
