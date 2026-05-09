package shift_lab.crm.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import shift_lab.crm.api.dto.request.transaction.TransactionCreateRequestDto;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.enums.ErrorCode;
import shift_lab.crm.core.enums.PaymentEnum;
import shift_lab.crm.core.exception.BusinessException;
import shift_lab.crm.core.repository.SellerRepository;
import shift_lab.crm.core.repository.TransactionRepository;
import shift_lab.crm.core.service.impl.TransactionServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionEntity createTransaction(Long id, SellerEntity seller)
    {
        return TransactionEntity.builder()
                .id(id)
                .seller(seller)
                .amount(BigDecimal.valueOf(5432))
                .paymentType(PaymentEnum.CARD)
                .transactionDate(LocalDateTime.now())
                .build();
    }

    private SellerEntity createSeller(Long sellerId) {
        return SellerEntity.builder()
                .id(sellerId)
                .name("Seller " + sellerId)
                .contactInfo("Contact Info " + sellerId)
                .registrationDate(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    @Test
    public void createTest() {
        SellerEntity seller = createSeller(1L);
        TransactionEntity transaction = createTransaction(1L, seller);

        when(sellerRepository.findByIsDeletedFalse(1L)).thenReturn(Optional.of(seller));
        when(transactionRepository.save(any())).thenReturn(transaction);

        TransactionEntity result = transactionService.create(TransactionCreateRequestDto.builder()
                        .sellerId(1L)
                        .amount(BigDecimal.valueOf(5432))
                        .paymentType(PaymentEnum.CARD)
                        .build());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(transaction.getId());
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(5432));
        assertThat(result.getPaymentType()).isEqualTo(PaymentEnum.CARD);
        assertThat(result.getSeller()).isEqualTo(seller);
        assertThat(result.getTransactionDate()).isNotNull();

        verify(sellerRepository).findByIsDeletedFalse(1L);
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    public void createTest_WithSellerNotFound() {
        assertThatThrownBy(() -> transactionService.create(TransactionCreateRequestDto.builder()
                        .sellerId(1L)
                        .amount(BigDecimal.valueOf(5432))
                        .paymentType(PaymentEnum.CARD)
                        .build())).isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SELLER_NOT_FOUND);
    }

    @Test
    public void findByIdTest() {
        SellerEntity seller = createSeller(1L);
        TransactionEntity transaction = createTransaction(1L, seller);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        TransactionEntity result = transactionService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(transaction.getId());
        assertThat(result.getAmount()).isEqualTo(BigDecimal.valueOf(5432));
        assertThat(result.getPaymentType()).isEqualTo(PaymentEnum.CARD);
        assertThat(result.getSeller()).isEqualTo(seller);
        assertThat(result.getTransactionDate()).isNotNull();

        verify(transactionRepository).findById(1L);
    }
}
