package shift_lab.crm.unit.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shift_lab.crm.api.dto.projections.TopSellerProjection;
import shift_lab.crm.api.dto.request.seller.SellerCreateRequestDto;
import shift_lab.crm.api.dto.request.seller.SellerPatchRequestDto;
import shift_lab.crm.api.dto.response.seller.BestSellerTimeResponseDto;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.enums.ErrorCode;
import shift_lab.crm.core.enums.PaymentEnum;
import shift_lab.crm.core.exception.BusinessException;
import shift_lab.crm.core.repository.SellerRepository;
import shift_lab.crm.core.repository.TransactionRepository;
import shift_lab.crm.core.service.impl.SellerServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SellerServiceTest {
    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SellerServiceImpl sellerService;

    private SellerEntity createSeller(Long sellerId) {
        return SellerEntity.builder()
                .id(sellerId)
                .name("Seller " + sellerId)
                .contactInfo("Contact Info " + sellerId)
                .registrationDate(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    private SellerEntity createDeletedSeller(Long sellerId) {
        return SellerEntity.builder()
                .id(sellerId)
                .name("Seller " + sellerId)
                .contactInfo("Contact Info " + sellerId)
                .registrationDate(LocalDateTime.now())
                .isDeleted(true)
                .build();
    }

    private TransactionEntity createTransaction(Long id, SellerEntity seller, LocalDateTime dateTime)
    {
        return TransactionEntity.builder()
                .id(id)
                .seller(seller)
                .amount(BigDecimal.valueOf(5432))
                .paymentType(PaymentEnum.CARD)
                .transactionDate(dateTime)
                .build();
    }

    @Test
    public void createTest() {
        SellerEntity sellerEntity = createSeller(1L);

        when(sellerRepository.save(any(SellerEntity.class))).thenReturn(sellerEntity);

        SellerCreateRequestDto sellerCreateRequestDto = SellerCreateRequestDto.builder()
                .name("Seller 1")
                .contactInfo("Contact Info 1")
                .build();

        SellerEntity result = sellerService.create(sellerCreateRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Seller 1");
        assertThat(result.getContactInfo()).isEqualTo("Contact Info 1");
        assertThat(result.getIsDeleted()).isEqualTo(false);
    }

    @Test
    public void getByIdTest_withValidIdAndExistingSellerEntity() {
        SellerEntity sellerEntity = createSeller(1L);

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(sellerEntity));

        SellerEntity result = sellerService.getById("1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Seller 1");
        assertThat(result.getContactInfo()).isEqualTo("Contact Info 1");
        assertThat(result.getIsDeleted()).isEqualTo(false);
    }

    @Test
    public void getByIdTest_withInvalidId() {
        assertThatThrownBy(() -> sellerService.getById("invalidId"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NUMBER_FORMAT_EXCEPTION);
    }

    @Test
    public void getByIdTest_withValidIdAndNonExistingSellerEntity() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sellerService.getById("1"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SELLER_NOT_FOUND);
    }

    @Test
    public void getAllSellersTest() {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );

        Pageable pageable = PageRequest.of(0, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, sellers.size());

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(0, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.hasPrevious()).isEqualTo(false);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getIsDeleted()).isEqualTo(false);
        assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getIsDeleted()).isEqualTo(false);
        assertThat(result.getContent().get(2).getId()).isEqualTo(3L);
        assertThat(result.getContent().get(2).getIsDeleted()).isEqualTo(false);
    }

    @Test
    public void getAllSellersTest_WithEmptyPage() {
        List<SellerEntity> sellers = List.of();
        Pageable pageable = PageRequest.of(0, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, sellers.size());

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(0, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(0);
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.hasPrevious()).isEqualTo(false);
    }

    @Test
    public void getAllSellersTest_WithSellersLessThanPageSize() {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L)
        );

        Pageable pageable = PageRequest.of(0, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, sellers.size());

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(0, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.hasPrevious()).isEqualTo(false);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getIsDeleted()).isEqualTo(false);
        assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getIsDeleted()).isEqualTo(false);
    }

    @Test
    public void getAllSellersTest_WithSellersMoreThanPageSize() {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );

        Pageable pageable = PageRequest.of(0, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, 4);

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(0, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.hasNext()).isEqualTo(true);
        assertThat(result.hasPrevious()).isEqualTo(false);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getIsDeleted()).isEqualTo(false);
        assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getIsDeleted()).isEqualTo(false);
        assertThat(result.getContent().get(2).getId()).isEqualTo(3L);
        assertThat(result.getContent().get(2).getIsDeleted()).isEqualTo(false);
    }

    @Test
    public void getAllSellersTest_WithLastPage() {
        List<SellerEntity> sellers = List.of(
                createSeller(4L)
        );

        Pageable pageable = PageRequest.of(1, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, 4);

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(1, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.hasPrevious()).isEqualTo(true);
        assertThat(result.getContent().get(0).getId()).isEqualTo(4L);
        assertThat(result.getContent().get(0).getIsDeleted()).isEqualTo(false);
    }

    @Test
    public void getAllSellersTest_WithMiddlePage() {
        List<SellerEntity> sellers = List.of(
                createSeller(4L),
                createSeller(5L),
                createSeller(6L)
        );

        Pageable pageable = PageRequest.of(1, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, 8);

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(1, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(8);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.hasNext()).isEqualTo(true);
        assertThat(result.hasPrevious()).isEqualTo(true);
        assertThat(result.getContent().get(0).getId()).isEqualTo(4L);
        assertThat(result.getContent().get(0).getIsDeleted()).isEqualTo(false);
        assertThat(result.getContent().get(1).getId()).isEqualTo(5L);
        assertThat(result.getContent().get(1).getIsDeleted()).isEqualTo(false);
        assertThat(result.getContent().get(2).getId()).isEqualTo(6L);
        assertThat(result.getContent().get(2).getIsDeleted()).isEqualTo(false);
    }

    @Test
    public void getAllDeletedSellersTest() {
        List<SellerEntity> sellers = List.of(
                createDeletedSeller(1L),
                createDeletedSeller(2L),
                createDeletedSeller(3L)
        );

        Pageable pageable = PageRequest.of(0, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, sellers.size());

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(0, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.hasPrevious()).isEqualTo(false);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getIsDeleted()).isEqualTo(true);
        assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getIsDeleted()).isEqualTo(true);
        assertThat(result.getContent().get(2).getId()).isEqualTo(3L);
        assertThat(result.getContent().get(2).getIsDeleted()).isEqualTo(true);
    }

    @Test
    public void getAllDeletedSellersTest_WithEmptyPage() {
        List<SellerEntity> sellers = List.of();
        Pageable pageable = PageRequest.of(0, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, sellers.size());

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(0, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(0);
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.hasPrevious()).isEqualTo(false);
    }

    @Test
    public void getAllDeletedSellersTest_WithSellersLessThanPageSize() {
        List<SellerEntity> sellers = List.of(
                createDeletedSeller(1L),
                createDeletedSeller(2L)
        );

        Pageable pageable = PageRequest.of(0, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, sellers.size());

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(0, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.hasPrevious()).isEqualTo(false);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getIsDeleted()).isEqualTo(true);
        assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getIsDeleted()).isEqualTo(true);
    }

    @Test
    public void getAllDeletedSellersTest_WithSellersMoreThanPageSize() {
        List<SellerEntity> sellers = List.of(
                createDeletedSeller(1L),
                createDeletedSeller(2L),
                createDeletedSeller(3L)
        );

        Pageable pageable = PageRequest.of(0, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, 4);

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(0, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.hasNext()).isEqualTo(true);
        assertThat(result.hasPrevious()).isEqualTo(false);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getIsDeleted()).isEqualTo(true);
        assertThat(result.getContent().get(1).getId()).isEqualTo(2L);
        assertThat(result.getContent().get(1).getIsDeleted()).isEqualTo(true);
        assertThat(result.getContent().get(2).getId()).isEqualTo(3L);
        assertThat(result.getContent().get(2).getIsDeleted()).isEqualTo(true);
    }

    @Test
    public void getAllDeletedSellersTest_WithLastPage() {
        List<SellerEntity> sellers = List.of(
                createDeletedSeller(4L)
        );

        Pageable pageable = PageRequest.of(1, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, 4);

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(1, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.hasNext()).isEqualTo(false);
        assertThat(result.hasPrevious()).isEqualTo(true);
        assertThat(result.getContent().get(0).getId()).isEqualTo(4L);
        assertThat(result.getContent().get(0).getIsDeleted()).isEqualTo(true);
    }

    @Test
    public void getAllDeletedSellersTest_WithPageEqualTwo() {
        List<SellerEntity> sellers = List.of(
                createDeletedSeller(4L),
                createDeletedSeller(5L),
                createDeletedSeller(6L)
        );

        Pageable pageable = PageRequest.of(1, 3);
        Page<SellerEntity> sellersPage = new PageImpl<>(sellers, pageable, 8);

        when(sellerRepository.findAllNotDeleted(pageable)).thenReturn(sellersPage);

        Page<SellerEntity> result = sellerService.getAllSellers(1, 3);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(8);
        assertThat(result.getTotalPages()).isEqualTo(3);
        assertThat(result.hasNext()).isEqualTo(true);
        assertThat(result.hasPrevious()).isEqualTo(true);
        assertThat(result.getContent().get(0).getId()).isEqualTo(4L);
        assertThat(result.getContent().get(0).getIsDeleted()).isEqualTo(true);
        assertThat(result.getContent().get(1).getId()).isEqualTo(5L);
        assertThat(result.getContent().get(1).getIsDeleted()).isEqualTo(true);
        assertThat(result.getContent().get(2).getId()).isEqualTo(6L);
        assertThat(result.getContent().get(2).getIsDeleted()).isEqualTo(true);
    }

    @Test
    public void updateTest_WithAllFields() {
        SellerEntity seller = createSeller(1L);
        SellerEntity updatedSeller = seller.toBuilder()
                .name("Updated name")
                .contactInfo("Updated contact info")
                .build();

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(SellerEntity.class))).thenReturn(updatedSeller);

        SellerEntity result = sellerService.update("1", SellerPatchRequestDto.builder()
                        .name("Updated name")
                        .contactInfo("Updated contact info")
                        .build());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated name");
        assertThat(result.getContactInfo()).isEqualTo("Updated contact info");
    }

    @Test
    public void updateTest_WithNameField() {
        SellerEntity seller = createSeller(1L);
        SellerEntity updatedSeller = seller.toBuilder()
                .name("Updated name")
                .build();

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(SellerEntity.class))).thenReturn(updatedSeller);

        SellerEntity result = sellerService.update("1", SellerPatchRequestDto.builder()
                .name("Updated name")
                .build());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated name");
        assertThat(result.getContactInfo()).isEqualTo("Contact Info 1");
    }

    @Test
    public void updateTest_WithContactInfoField() {
        SellerEntity seller = createSeller(1L);
        SellerEntity updatedSeller = seller.toBuilder()
                .contactInfo("Updated contact info")
                .build();

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(SellerEntity.class))).thenReturn(updatedSeller);

        SellerEntity result = sellerService.update("1", SellerPatchRequestDto.builder()
                .contactInfo("Updated contact info")
                .build());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Seller 1");
        assertThat(result.getContactInfo()).isEqualTo("Updated contact info");
    }

    @Test
    public void updateTest_WithEmptyFields() {
        SellerEntity seller = createSeller(1L);

        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(SellerEntity.class))).thenReturn(seller);

        SellerEntity result = sellerService.update("1", SellerPatchRequestDto.builder().build());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Seller 1");
        assertThat(result.getContactInfo()).isEqualTo("Contact Info 1");
    }

    @Test
    public void deleteTest() {
        SellerEntity seller = createSeller(1L);
        SellerEntity deletedSeller = seller.toBuilder()
                        .isDeleted(true)
                        .build();
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(sellerRepository.save(any(SellerEntity.class))).thenReturn(deletedSeller);

        String result = sellerService.delete("1");
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("Продавец успешно удален");
    }

    @Test
    public void getTopSellerTest() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        TopSellerProjection mockTopSellerProjection = mock(TopSellerProjection.class);
        SellerEntity mockTopSeller =  mock(SellerEntity.class);

        when(transactionRepository.findTopSellerByPeriod(startDate, endDate)).thenReturn(mockTopSellerProjection);
        when(mockTopSellerProjection.getSeller()).thenReturn(mockTopSeller);
        when(mockTopSellerProjection.getTotalAmount()).thenReturn(BigDecimal.valueOf(9999));

        TopSellerProjection topSellerProjection = sellerService.getTopSeller(startDate, endDate);
        assertThat(topSellerProjection).isNotNull();
        assertThat(topSellerProjection.getSeller()).isEqualTo(mockTopSeller);
        assertThat(topSellerProjection.getTotalAmount()).isEqualTo(BigDecimal.valueOf(9999));
    }

    @Test
    public void getTopSellerTest_WithEmptyTopSeller() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        assertThatThrownBy(() -> sellerService.getTopSeller(startDate, endDate))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SELLER_NOT_FOUND);
    }

    @Test
    public void getBestSellerTimeTest() {
        SellerEntity seller = createSeller(1L);
        List<TransactionEntity> transactions = List.of(
                createTransaction(1L, seller, LocalDateTime.now().minusDays(5)),
                createTransaction(2L, seller, LocalDateTime.now().minusDays(5)),
                createTransaction(3L, seller, LocalDateTime.now().minusDays(4)),
                createTransaction(4L, seller, LocalDateTime.now().minusDays(2)),
                createTransaction(5L, seller, LocalDateTime.now().minusDays(1))
        );

        LocalDateTime startDate = LocalDateTime.now().minusDays(6);
        LocalDateTime endDate = LocalDateTime.now();

        when(transactionRepository.findSellerTransactionByPeriod(seller.getId(), startDate, endDate)).thenReturn(transactions);
        BestSellerTimeResponseDto result = sellerService.getBestSellerTime(seller.getId().toString(), startDate, endDate);
        assertThat(result).isNotNull();
        assertThat(result.transactionCount()).isEqualTo(3);
        assertThat(result.startBestTime()).isEqualTo(transactions.get(0).getTransactionDate().toLocalDate());
        assertThat(result.endBestTime()).isEqualTo(transactions.get(2).getTransactionDate().toLocalDate());
    }

    @Test
    public void getBestSellerTimeTest_WithEmptyTransactionList() {
        assertThatThrownBy(() -> sellerService.getBestSellerTime("1", LocalDateTime.now().minusDays(6), LocalDateTime.now()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.EMPTY_LIST);
    }

    @Test
    public void getBestSellerTimeTest_WithInvalidSellerId() {
        assertThatThrownBy(() -> sellerService.getBestSellerTime("abc", LocalDateTime.now().minusDays(6), LocalDateTime.now()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NUMBER_FORMAT_EXCEPTION);
    }

    @Test
    public void getBestSellerTimeTest_WithTheSameBestTime() {
        SellerEntity seller = createSeller(1L);
        List<TransactionEntity> transactions = List.of(
                createTransaction(1L, seller, LocalDateTime.now().minusDays(5)),
                createTransaction(2L, seller, LocalDateTime.now().minusDays(5)),
                createTransaction(3L, seller, LocalDateTime.now().minusDays(4)),
                createTransaction(4L, seller, LocalDateTime.now().minusDays(2)),
                createTransaction(5L, seller, LocalDateTime.now().minusDays(1)),
                createTransaction(6L, seller, LocalDateTime.now().minusDays(1))
        );

        LocalDateTime startDate = LocalDateTime.now().minusDays(6);
        LocalDateTime endDate = LocalDateTime.now();

        when(transactionRepository.findSellerTransactionByPeriod(seller.getId(), startDate, endDate)).thenReturn(transactions);
        BestSellerTimeResponseDto result = sellerService.getBestSellerTime(seller.getId().toString(), startDate, endDate);
        assertThat(result).isNotNull();
        assertThat(result.transactionCount()).isEqualTo(3);
        assertThat(result.startBestTime()).isEqualTo(transactions.get(0).getTransactionDate().toLocalDate());
        assertThat(result.endBestTime()).isEqualTo(transactions.get(2).getTransactionDate().toLocalDate());
    }
}
