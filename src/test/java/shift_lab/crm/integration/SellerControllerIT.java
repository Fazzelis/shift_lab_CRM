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
import shift_lab.crm.api.dto.request.seller.SellerCreateRequestDto;
import shift_lab.crm.api.dto.request.seller.SellerPatchRequestDto;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.enums.PaymentEnum;
import shift_lab.crm.core.repository.SellerRepository;
import shift_lab.crm.core.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class SellerControllerIT {
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

    private SellerEntity createDeletedSeller(Long id) {
        return SellerEntity.builder()
                .name("Name " + id)
                .contactInfo("Contact info " + id)
                .registrationDate(LocalDateTime.now())
                .isDeleted(true)
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
    public void createSellerTest() throws Exception {
        SellerCreateRequestDto sellerDto = new SellerCreateRequestDto("Test name", "Contact info");
        mockMvc.perform(post("/seller")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sellerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test name"))
                .andExpect(jsonPath("$.contactInfo").value("Contact info"))
                .andExpect(jsonPath("$.registrationDate").exists());
    }

    @Test
    public void getSellerByIdTest_NotFound() throws Exception {
        mockMvc.perform(get("/seller/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Продавец не найден"))
                .andExpect(jsonPath("$.instance").value("/seller/1"));
    }

    @Test
    public void getSellerByIdTest_BadRequest() throws Exception {
        mockMvc.perform(get("/seller/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Неверный формат id"))
                .andExpect(jsonPath("$.instance").value("/seller/abc"));
    }

    @Test
    public void getSellerById() throws Exception {
        SellerEntity seller = createSeller(1L);
        SellerEntity savedSeller = sellerRepository.save(seller);
        mockMvc.perform(get("/seller/{id}", savedSeller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Name 1"))
                .andExpect(jsonPath("$.contactInfo").value("Contact info 1"))
                .andExpect(jsonPath("$.registrationDate").exists());
    }

    @Test
    public void getAllSellersTest() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);
        mockMvc.perform(get("/seller/all?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.sellers").isArray());
    }

    @Test
    public void getAllSellersTest_WithEmptyPage() throws Exception {
        mockMvc.perform(get("/seller/all?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.sellers").isEmpty());
    }

    @Test
    public void getAllSellersTest_WithSellersLessThanPageSize() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L)
        );
        sellerRepository.saveAll(sellers);
        mockMvc.perform(get("/seller/all?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.sellers").isArray());
    }

    @Test
    public void getAllSellersTest_WithSellerMoreThanPageSize() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L),
                createSeller(4L)
        );
        sellerRepository.saveAll(sellers);
        mockMvc.perform(get("/seller/all?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.sellers").isArray());
    }

    @Test
    public void getAllSellersTest_WithLastPage() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L),
                createSeller(4L)
        );
        sellerRepository.saveAll(sellers);
        mockMvc.perform(get("/seller/all?page=1&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(true))
                .andExpect(jsonPath("$.sellers").isArray());
    }

    @Test
    public void getAllSellersTest_WithMiddlePage() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L),
                createSeller(4L),
                createSeller(5L),
                createSeller(6L),
                createSeller(7L),
                createSeller(8L),
                createSeller(9L)
        );
        sellerRepository.saveAll(sellers);
        mockMvc.perform(get("/seller/all?page=1&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(9))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(true))
                .andExpect(jsonPath("$.sellers").isArray());
    }

    @Test
    public void getAllDeletedSellersTest() throws Exception {
        List<SellerEntity> sellers = List.of(
                createDeletedSeller(1L),
                createDeletedSeller(2L),
                createDeletedSeller(3L)
        );
        sellerRepository.saveAll(sellers);
        mockMvc.perform(get("/seller/all/deleted?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.sellers").isArray());
    }

    @Test
    public void getAllDeletedSellersTest_WithEmptyPage() throws Exception {
        mockMvc.perform(get("/seller/all/deleted?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.sellers").isEmpty());
    }

    @Test
    public void getAllDeletedSellersTest_WithSellersLessThanPageSize() throws Exception {
        List<SellerEntity> sellers = List.of(
                createDeletedSeller(1L),
                createDeletedSeller(2L)
        );
        sellerRepository.saveAll(sellers);
        mockMvc.perform(get("/seller/all/deleted?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.sellers").isArray());
    }

    @Test
    public void getAllDeletedSellersTest_WithSellerMoreThanPageSize() throws Exception {
        List<SellerEntity> sellers = List.of(
                createDeletedSeller(1L),
                createDeletedSeller(2L),
                createDeletedSeller(3L),
                createDeletedSeller(4L)
        );
        sellerRepository.saveAll(sellers);
        mockMvc.perform(get("/seller/all/deleted?page=0&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.sellers").isArray());
    }

    @Test
    public void getAllDeletedSellersTest_WithLastPage() throws Exception {
        List<SellerEntity> sellers = List.of(
                createDeletedSeller(1L),
                createDeletedSeller(2L),
                createDeletedSeller(3L),
                createDeletedSeller(4L)
        );
        sellerRepository.saveAll(sellers);
        mockMvc.perform(get("/seller/all/deleted?page=1&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(4))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(true))
                .andExpect(jsonPath("$.sellers").isArray());
    }

    @Test
    public void getAllDeletedSellersTest_WithMiddlePage() throws Exception {
        List<SellerEntity> sellers = List.of(
                createDeletedSeller(1L),
                createDeletedSeller(2L),
                createDeletedSeller(3L),
                createDeletedSeller(4L),
                createDeletedSeller(5L),
                createDeletedSeller(6L),
                createDeletedSeller(7L),
                createDeletedSeller(8L),
                createDeletedSeller(9L)
        );
        sellerRepository.saveAll(sellers);
        mockMvc.perform(get("/seller/all/deleted?page=1&size=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(9))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(true))
                .andExpect(jsonPath("$.sellers").isArray());
    }

    @Test
    public void updateTest() throws Exception {
        SellerEntity seller = sellerRepository.save(createSeller(1L));
        SellerPatchRequestDto patchRequestDto = SellerPatchRequestDto.builder()
                .name("Updated name")
                .contactInfo("Updated contact info")
                .build();
        mockMvc.perform(patch("/seller/{id}", seller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(seller.getId()))
                .andExpect(jsonPath("$.name").value("Updated name"))
                .andExpect(jsonPath("$.contactInfo").value("Updated contact info"))
                .andExpect(jsonPath("$.registrationDate").exists());
    }
    @Test
    public void deleteSellerTest() throws Exception {
        SellerEntity seller = sellerRepository.save(createSeller(1L));

        mockMvc.perform(delete("/seller/delete/{id}",  seller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Продавец успешно удален"));
    }

    @Test
    public void getTopSellerTest() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusDays(2)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusDays(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller?startDate={startDate}&endDate={endDate}",
                LocalDateTime.now().minusDays(2), LocalDateTime.now()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").exists())
                .andExpect(jsonPath("$.seller.id").value(sellers.get(1).getId()))
                .andExpect(jsonPath("totalAmount").value(110));
    }

    @Test
    public void getTopSellerTest_WithNotFound() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusDays(2)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusDays(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller?startDate={startDate}&endDate={endDate}",
                        LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Самый продуктивный продавец за указанную дату не найден"));
    }

    @Test
    public void getTopSellerTest_WithTwoTopSellers() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(1), BigDecimal.valueOf(50), PaymentEnum.CARD, LocalDateTime.now()),
                createTransaction(sellers.get(1), BigDecimal.valueOf(50), PaymentEnum.TRANSFER, LocalDateTime.now()),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller?startDate={startDate}&endDate={endDate}",
                        LocalDateTime.now().minusDays(1), LocalDateTime.now()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").exists())
                .andExpect(jsonPath("$.seller.id").value(sellers.get(1).getId()))
                .andExpect(jsonPath("totalAmount").value(100));
    }

    @Test
    public void getTopSellerDayTest() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusDays(2)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusDays(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").exists())
                .andExpect(jsonPath("$.seller.id").value(sellers.get(2).getId()))
                .andExpect(jsonPath("totalAmount").value(100));
    }

    @Test
    public void getTopSellerDayTest_WithNotFound() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusDays(2)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusDays(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now().minusDays(1))
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/day"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Самый продуктивный продавец за указанную дату не найден"));
    }

    @Test
    public void getTopSellerDayTest_WithTwoTopSellers() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);
        LocalDateTime date = LocalDateTime.now();

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(1), BigDecimal.valueOf(50), PaymentEnum.CARD, date),
                createTransaction(sellers.get(1), BigDecimal.valueOf(50), PaymentEnum.TRANSFER, date),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, date)
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").exists())
                .andExpect(jsonPath("$.seller.id").value(sellers.get(1).getId()))
                .andExpect(jsonPath("totalAmount").value(100));
    }

    @Test
    public void getTopSellerMonthTest() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusMonths(2)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusMonths(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusMonths(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusMonths(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusMonths(1)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").exists())
                .andExpect(jsonPath("$.seller.id").value(sellers.get(2).getId()))
                .andExpect(jsonPath("totalAmount").value(100));
    }

    @Test
    public void getTopSellerMonthTest_WithNotFound() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusMonths(2)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusMonths(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusMonths(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusMonths(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusMonths(1)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now().minusMonths(1))
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/month"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Самый продуктивный продавец за указанную дату не найден"));
    }

    @Test
    public void getTopSellerMonthTest_WithTwoTopSellers() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);
        LocalDateTime date = LocalDateTime.now().minusDays(20);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(1), BigDecimal.valueOf(50), PaymentEnum.CARD, date),
                createTransaction(sellers.get(1), BigDecimal.valueOf(50), PaymentEnum.TRANSFER, date),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, date)
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").exists())
                .andExpect(jsonPath("$.seller.id").value(sellers.get(1).getId()))
                .andExpect(jsonPath("totalAmount").value(100));
    }

    @Test
    public void getTopSellerQuarterTest() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusMonths(1)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusMonths(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusMonths(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusMonths(3).minusDays(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusMonths(4)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now().minusMonths(1))
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/quarter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").exists())
                .andExpect(jsonPath("$.seller.id").value(sellers.get(2).getId()))
                .andExpect(jsonPath("totalAmount").value(100));
    }

    @Test
    public void getTopSellerQuarterTest_WithNotFound() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusMonths(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusMonths(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusMonths(4)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusMonths(5)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusMonths(4)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now().minusMonths(3))
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/quarter"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Самый продуктивный продавец за указанную дату не найден"));
    }

    @Test
    public void getTopSellerQuarterTest_WithTwoTopSellers() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);
        LocalDateTime date = LocalDateTime.now().minusMonths(2);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(1), BigDecimal.valueOf(50), PaymentEnum.CARD, date),
                createTransaction(sellers.get(1), BigDecimal.valueOf(50), PaymentEnum.TRANSFER, date),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, date)
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/quarter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").exists())
                .andExpect(jsonPath("$.seller.id").value(sellers.get(1).getId()))
                .andExpect(jsonPath("totalAmount").value(100));
    }
//    qwerty

    @Test
    public void getTopSellerYearTest() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusYears(1)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusYears(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusYears(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusYears(3).minusDays(1)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusYears(4)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now().minusMonths(11))
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/year"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").exists())
                .andExpect(jsonPath("$.seller.id").value(sellers.get(2).getId()))
                .andExpect(jsonPath("totalAmount").value(100));
    }

    @Test
    public void getTopSellerYearTest_WithNotFound() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusYears(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusYears(1)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusYears(4)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusYears(5)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusYears(4)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now().minusYears(1))
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/year"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Самый продуктивный продавец за указанную дату не найден"));
    }

    @Test
    public void getTopSellerYearTest_WithTwoTopSellers() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);
        LocalDateTime date = LocalDateTime.now().minusMonths(10);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(1), BigDecimal.valueOf(50), PaymentEnum.CARD, date),
                createTransaction(sellers.get(1), BigDecimal.valueOf(50), PaymentEnum.TRANSFER, date),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, date)
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/top-seller/year"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seller").exists())
                .andExpect(jsonPath("$.seller.id").value(sellers.get(1).getId()))
                .andExpect(jsonPath("totalAmount").value(100));
    }

    @Test
    public void getSellersWithAmountBelowTest() throws Exception {
        List<SellerEntity> sellers = List.of(
                createSeller(1L),
                createSeller(2L),
                createSeller(3L)
        );
        sellerRepository.saveAll(sellers);

        List<TransactionEntity> transactions = List.of(
                createTransaction(sellers.get(0), BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusDays(3)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusDays(1)),
                createTransaction(sellers.get(0), BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(4)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusDays(5)),
                createTransaction(sellers.get(1), BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(4)),
                createTransaction(sellers.get(2), BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now().minusDays(1))
        );
        transactionRepository.saveAll(transactions);
        String requestUrl = "/seller/amount-below?startDate={startDate}&endDate={endDate}&amount={amount}&page={page}&size={size}";
        mockMvc.perform(get(requestUrl,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusDays(1),
                BigDecimal.valueOf(101),
                0,
                5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.sellers").isArray())
                .andExpect(jsonPath("$.sellers[0].id").value(sellers.get(0).getId()))
                .andExpect(jsonPath("$.sellers[1].id").value(sellers.get(2).getId()));
    }

    @Test
    public void getSellersWithAmountBelowTest_WithEmptyResult() throws Exception {
        String requestUrl = "/seller/amount-below?startDate={startDate}&endDate={endDate}&amount={amount}&page={page}&size={size}";
        mockMvc.perform(get(requestUrl,
                        LocalDateTime.now().minusMonths(1),
                        LocalDateTime.now().minusDays(1),
                        BigDecimal.valueOf(101),
                        0,
                        5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.hasPrevious").value(false))
                .andExpect(jsonPath("$.sellers").isEmpty());
    }

    @Test
    public void getBestSellerTime() throws Exception {
        SellerEntity seller = createSeller(1L);
        SellerEntity saved_seller = sellerRepository.save(seller);

        List<TransactionEntity> transactions = List.of(
                createTransaction(seller, BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusDays(6)),
                createTransaction(seller, BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusDays(4)),
                createTransaction(seller, BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(4)),
                createTransaction(seller, BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusDays(2)),
                createTransaction(seller, BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(2)),
                createTransaction(seller, BigDecimal.valueOf(100), PaymentEnum.CARD, LocalDateTime.now())
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/{id}/best-time?startDate={startDate}&endDate={endDate}", saved_seller.getId(), LocalDateTime.now().minusDays(6), LocalDateTime.now()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionCount").value(2))
                .andExpect(jsonPath("$.startBestTime").value(transactions.get(1).getTransactionDate().toLocalDate().toString()))
                .andExpect(jsonPath("$.endBestTime").value(transactions.get(2).getTransactionDate().toLocalDate().toString()));
    }

    @Test
    public void getBestSellerTime_WithoutTransactions() throws Exception {
        SellerEntity seller = createSeller(1L);
        SellerEntity saved_seller = sellerRepository.save(seller);

        List<TransactionEntity> transactions = List.of(
                createTransaction(seller, BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusDays(6)),
                createTransaction(seller, BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusDays(4)),
                createTransaction(seller, BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(4)),
                createTransaction(seller, BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusDays(2)),
                createTransaction(seller, BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(2))
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/{id}/best-time?startDate={startDate}&endDate={endDate}", saved_seller.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Транзакции не найдены"));
    }

    @Test
    public void getBestSellerTime_WithInvalidSellerId() throws Exception {
        SellerEntity seller = createSeller(1L);
        sellerRepository.save(seller);

        List<TransactionEntity> transactions = List.of(
                createTransaction(seller, BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusDays(6)),
                createTransaction(seller, BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusDays(4)),
                createTransaction(seller, BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(4)),
                createTransaction(seller, BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusDays(2)),
                createTransaction(seller, BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(2))
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/1abc1/best-time?startDate={startDate}&endDate={endDate}", LocalDateTime.now().minusDays(6), LocalDateTime.now()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Неверный формат числа"));
    }

    @Test
    public void getBestSellerTime_WithTwoValidPeriods() throws Exception {
        SellerEntity seller = createSeller(1L);
        SellerEntity saved_seller = sellerRepository.save(seller);

        List<TransactionEntity> transactions = List.of(
                createTransaction(seller, BigDecimal.valueOf(45), PaymentEnum.CARD, LocalDateTime.now().minusDays(6)),
                createTransaction(seller, BigDecimal.valueOf(15), PaymentEnum.CASH, LocalDateTime.now().minusDays(5)),
                createTransaction(seller, BigDecimal.valueOf(5), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(4)),
                createTransaction(seller, BigDecimal.valueOf(55), PaymentEnum.CARD, LocalDateTime.now().minusDays(2)),
                createTransaction(seller, BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(2)),
                createTransaction(seller, BigDecimal.valueOf(55), PaymentEnum.TRANSFER, LocalDateTime.now().minusDays(2))
        );
        transactionRepository.saveAll(transactions);

        mockMvc.perform(get("/seller/{id}/best-time?startDate={startDate}&endDate={endDate}", saved_seller.getId(), LocalDateTime.now().minusDays(7), LocalDateTime.now()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionCount").value(3))
                .andExpect(jsonPath("$.startBestTime").value(transactions.get(0).getTransactionDate().toLocalDate().toString()))
                .andExpect(jsonPath("$.endBestTime").value(transactions.get(2).getTransactionDate().toLocalDate().toString()));
    }
}
