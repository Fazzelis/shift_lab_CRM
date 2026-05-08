package shift_lab.crm.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shift_lab.crm.api.dto.projections.SellerBelowAmountProjection;
import shift_lab.crm.api.dto.projections.TopSellerProjection;
import shift_lab.crm.api.dto.request.seller.SellerCreateRequestDto;
import shift_lab.crm.api.dto.request.seller.SellerPatchRequestDto;
import shift_lab.crm.api.dto.response.BasicResponseDto;
import shift_lab.crm.api.dto.response.seller.*;
import shift_lab.crm.api.mapper.SellerMapper;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.service.SellerService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping("/seller")
public class SellerController {
    private SellerService sellerService;
    private SellerMapper sellerMapper;

    @PostMapping
    public ResponseEntity<SellerResponseDto> create(@RequestBody SellerCreateRequestDto sellerDto) {
        SellerResponseDto createdSeller = sellerMapper.map(sellerService.create(sellerDto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdSeller);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponseDto> getSellerById(@PathVariable String id) {
        SellerEntity seller = sellerService.getById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sellerMapper.map(seller));
    }

    @GetMapping("/all")
    public ResponseEntity<SellersResponseDto> getAllSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SellerEntity> sellers = sellerService.getAllSellers(page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SellersResponseDto.builder()
                        .sellers(sellers.stream()
                                .map(s -> sellerMapper.map(s))
                                .toList())
                        .totalPages(sellers.getTotalPages())
                        .totalElements(sellers.getTotalElements())
                        .hasNext(sellers.hasNext())
                        .hasPrevious(sellers.hasPrevious())
                        .build());
    }

    @GetMapping("/all/deleted")
    public ResponseEntity<SellersResponseDto> getAllDeletedSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SellerEntity> sellers = sellerService.getAllDeletedSellers(page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SellersResponseDto.builder()
                        .sellers(sellers.stream()
                                .map(s -> sellerMapper.map(s))
                                .toList())
                        .totalPages(sellers.getTotalPages())
                        .totalElements(sellers.getTotalElements())
                        .hasNext(sellers.hasNext())
                        .hasPrevious(sellers.hasPrevious())
                        .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SellerResponseDto> update(
            @PathVariable(name = "id") String id,
            @RequestBody SellerPatchRequestDto patchRequestDto
            ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sellerMapper.map(sellerService.update(id, patchRequestDto)));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<BasicResponseDto> deleteSeller(@PathVariable String id) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BasicResponseDto.builder()
                            .message(sellerService.delete(id))
                            .build());
    }

    @GetMapping("/top-seller")
    public ResponseEntity<TopSellerResponseDto> getTopSeller(
            @RequestParam() @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam() @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        TopSellerProjection topSellerProjection = sellerService.getTopSeller(startDate, endDate);
        SellerResponseDto sellerResponseDto = SellerResponseDto.builder()
                .id(topSellerProjection.getSeller().getId())
                .name(topSellerProjection.getSeller().getName())
                .contactInfo(topSellerProjection.getSeller().getContactInfo())
                .registrationDate(topSellerProjection.getSeller().getRegistrationDate())
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TopSellerResponseDto.builder()
                        .seller(sellerResponseDto)
                        .totalAmount(topSellerProjection.getTotalAmount())
                        .build());
    }

    @GetMapping("/top-seller/day")
    public ResponseEntity<TopSellerResponseDto> getTopSellerDay() {
        TopSellerProjection topSellerProjection = sellerService.getTopSeller(LocalDateTime.now().minusDays(1), LocalDateTime.now());
        SellerResponseDto sellerResponseDto = SellerResponseDto.builder()
                .id(topSellerProjection.getSeller().getId())
                .name(topSellerProjection.getSeller().getName())
                .contactInfo(topSellerProjection.getSeller().getContactInfo())
                .registrationDate(topSellerProjection.getSeller().getRegistrationDate())
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TopSellerResponseDto.builder()
                        .seller(sellerResponseDto)
                        .totalAmount(topSellerProjection.getTotalAmount())
                        .build());
    }

    @GetMapping("/top-seller/month")
    public ResponseEntity<TopSellerResponseDto> getTopSellerMonth() {
        TopSellerProjection topSellerProjection = sellerService.getTopSeller(LocalDateTime.now().minusMonths(1), LocalDateTime.now());
        SellerResponseDto sellerResponseDto = SellerResponseDto.builder()
                .id(topSellerProjection.getSeller().getId())
                .name(topSellerProjection.getSeller().getName())
                .contactInfo(topSellerProjection.getSeller().getContactInfo())
                .registrationDate(topSellerProjection.getSeller().getRegistrationDate())
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TopSellerResponseDto.builder()
                        .seller(sellerResponseDto)
                        .totalAmount(topSellerProjection.getTotalAmount())
                        .build());
    }

    @GetMapping("/top-seller/quarter")
    public ResponseEntity<TopSellerResponseDto> getTopSellerQuarter() {
        TopSellerProjection topSellerProjection = sellerService.getTopSeller(LocalDateTime.now().minusMonths(3), LocalDateTime.now());
        SellerResponseDto sellerResponseDto = SellerResponseDto.builder()
                .id(topSellerProjection.getSeller().getId())
                .name(topSellerProjection.getSeller().getName())
                .contactInfo(topSellerProjection.getSeller().getContactInfo())
                .registrationDate(topSellerProjection.getSeller().getRegistrationDate())
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TopSellerResponseDto.builder()
                        .seller(sellerResponseDto)
                        .totalAmount(topSellerProjection.getTotalAmount())
                        .build());
    }

    @GetMapping("/top-seller/year")
    public ResponseEntity<TopSellerResponseDto> getTopSellerYear() {
        TopSellerProjection topSellerProjection = sellerService.getTopSeller(LocalDateTime.now().minusYears(1), LocalDateTime.now());
        SellerResponseDto sellerResponseDto = SellerResponseDto.builder()
                .id(topSellerProjection.getSeller().getId())
                .name(topSellerProjection.getSeller().getName())
                .contactInfo(topSellerProjection.getSeller().getContactInfo())
                .registrationDate(topSellerProjection.getSeller().getRegistrationDate())
                .build();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TopSellerResponseDto.builder()
                        .seller(sellerResponseDto)
                        .totalAmount(topSellerProjection.getTotalAmount())
                        .build());
    }

    @GetMapping("/amount-below")
    public ResponseEntity<SellersBelowAmountResponseDto> getSellersWithAmountBelow(
            @RequestParam(defaultValue = "1000") BigDecimal amount,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ) {
        Page<SellerBelowAmountProjection> sellerProjections = sellerService.getSellersBelow(
                amount,
                startDate,
                endDate,
                page,
                size
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SellersBelowAmountResponseDto.builder()
                        .totalElements(sellerProjections.getTotalElements())
                        .totalPages(sellerProjections.getTotalPages())
                        .hasNext(sellerProjections.hasNext())
                        .hasPrevious(sellerProjections.hasPrevious())
                        .sellers(sellerProjections.stream()
                                .map(s -> SellerBelowAmountResponseDto.builder()
                                        .id(s.getSeller().getId())
                                        .name(s.getSeller().getName())
                                        .contactInfo(s.getSeller().getContactInfo())
                                        .registrationDate(s.getSeller().getRegistrationDate())
                                        .amount(s.getTotalAmount())
                                        .build())
                                .toList())
                        .build());
    }
}
