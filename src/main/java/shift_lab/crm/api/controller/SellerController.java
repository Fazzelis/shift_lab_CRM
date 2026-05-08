package shift_lab.crm.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shift_lab.crm.api.dto.request.seller.SellerCreateRequestDto;
import shift_lab.crm.api.dto.request.seller.SellerPatchRequestDto;
import shift_lab.crm.api.dto.response.BasicResponseDto;
import shift_lab.crm.api.dto.response.seller.SellerResponseDto;
import shift_lab.crm.api.dto.response.seller.SellersResponseDto;
import shift_lab.crm.api.mapper.SellerMapper;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.service.SellerService;

import java.util.List;
import java.util.Optional;

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

    @DeleteMapping("delete/{id}")
    public ResponseEntity<BasicResponseDto> deleteSeller(@PathVariable String id) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BasicResponseDto.builder()
                            .message(sellerService.delete(id))
                            .build());
    }
}
