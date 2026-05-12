package shift_lab.crm.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shift_lab.crm.api.dto.request.transaction.TransactionCreateRequestDto;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.enums.PaymentEnum;
import shift_lab.crm.core.repository.SellerRepository;
import shift_lab.crm.core.repository.TransactionRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    private SellerEntity createSeller(Long id) {
        return SellerEntity.builder()
                .name("Name " + id)
                .contactInfo("Contact info " + id)
                .registrationDate(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    private TransactionEntity createTransaction(
            SellerEntity seller,
            BigDecimal amount,
            PaymentEnum paymentType,
            LocalDateTime transactionDate
    ) {
        return TransactionEntity.builder()
                .seller(seller)
                .amount(amount)
                .paymentType(paymentType)
                .transactionDate(transactionDate)
                .build();
    }

    @Test
    public void createTest() throws Exception {
        SellerEntity seller = createSeller(1L);
        SellerEntity saved_seller = sellerRepository.save(seller);

        TransactionCreateRequestDto requestDto = TransactionCreateRequestDto.builder()
                .sellerId(saved_seller.getId())
                .amount(BigDecimal.valueOf(123))
                .paymentType(PaymentEnum.CARD)
                .build();

        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sellerId").value(seller.getId()))
                .andExpect(jsonPath("$.amount").value(BigDecimal.valueOf(123)))
                .andExpect(jsonPath("$.paymentType").value("CARD"))
                .andExpect(jsonPath("$.transactionDate").exists());
    }

    @Test
    public void createTest_WithNotFound() throws Exception {
        TransactionCreateRequestDto requestDto = TransactionCreateRequestDto.builder()
                .sellerId(1L)
                .amount(BigDecimal.valueOf(123))
                .paymentType(PaymentEnum.CARD)
                .build();

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Продавец не найден"));
    }

    @Test
    public void getAllTest() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(123), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(31), PaymentEnum.CARD, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(51), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(23), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(32), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(1), BigDecimal.valueOf(43), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/transaction/all?page=0&size=6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.transactions").isArray());
    }

    @Test
    public void getAllTest_WithEmptyPage() throws Exception {
        mockMvc.perform(get("/transaction/all?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.transactions").isEmpty());
    }

    @Test
    public void getAllTest_WithSellersLessThanPageSize() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(123), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(31), PaymentEnum.CARD, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(51), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(23), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(32), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(1), BigDecimal.valueOf(43), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/transaction/all?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.transactions").isArray());
    }

    @Test
    public void getAllTest_WithSellerMoreThanPageSize() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(123), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(31), PaymentEnum.CARD, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(51), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(23), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(32), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(1), BigDecimal.valueOf(43), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/transaction/all?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.transactions").isArray());
    }

    @Test
    public void getAllTest_WithLastPage() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(123), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(31), PaymentEnum.CARD, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(51), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(23), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(32), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(1), BigDecimal.valueOf(43), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/transaction/all?page=1&size=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(true))
                .andExpect(jsonPath("$.transactions").isArray());
    }

    @Test
    public void getAllTest_WithMiddlePage() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(123), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(31), PaymentEnum.CARD, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(51), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(23), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(32), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(1), BigDecimal.valueOf(43), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/transaction/all?page=1&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(true))
                .andExpect(jsonPath("$.transactions").isArray());
    }

    @Test
    public void getByIdTest() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(123), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(31), PaymentEnum.CARD, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(51), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(23), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(32), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(1), BigDecimal.valueOf(43), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/transaction/{id}", transactions.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactions.get(0).getId()))
                .andExpect(jsonPath("$.sellerId").value(sellers.get(0).getId()))
                .andExpect(jsonPath("$.amount").value(123))
                .andExpect(jsonPath("$.paymentType").value("CASH"))
                .andExpect(jsonPath("$.transactionDate").exists());
    }

    @Test
    public void getByIdTest_WithNotFound() throws Exception {
        mockMvc.perform(get("/transaction/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Транзакция не найдена"));
    }

    @Test
    public void getAllBySellerIdTest() throws  Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(123), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(31), PaymentEnum.CARD, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(51), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(23), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(32), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(1), BigDecimal.valueOf(43), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/transaction/seller-id/{id}?page=0&size=2", sellers.get(0).getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.transactions").isArray());
    }

    @Test
    public void getAllBySellerIdTest_WithNonExistentSeller() throws  Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(123), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(31), PaymentEnum.CARD, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(51), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(0), BigDecimal.valueOf(23), PaymentEnum.CASH, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(32), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(1), BigDecimal.valueOf(43), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/transaction/seller-id/9999999999999999?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.transactions").isEmpty());
    }
}
