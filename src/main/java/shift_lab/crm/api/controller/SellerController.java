package shift_lab.crm.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shift_lab.crm.api.dto.request.seller.SellerCreateRequestDto;
import shift_lab.crm.api.dto.request.seller.SellerPatchRequestDto;
import shift_lab.crm.api.dto.response.seller.SellerResponseDto;
import shift_lab.crm.api.mapper.SellerMapper;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.service.SellerService;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("seller")
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
        Optional<SellerEntity> optionalSeller = sellerService.getById(id);
        if (optionalSeller.isPresent()) {
            SellerResponseDto sellerResponseDto = sellerMapper.map(optionalSeller.get());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(sellerResponseDto);
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SellerResponseDto>> getAllSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<SellerResponseDto> sellers = sellerService.getAllSellers(page, size).stream()
                .filter(seller -> !seller.getIsDeleted())
                .map(seller -> sellerMapper.map(seller))
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sellers);
    }

    @GetMapping("/all/deleted")
    public ResponseEntity<List<SellerResponseDto>> getAllDeletedSellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<SellerResponseDto> sellers = sellerService.getAllDeletedSellers(page, size).stream()
                .map(seller -> sellerMapper.map(seller))
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sellers);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SellerResponseDto> update(
            @PathVariable(name = "id") String id,
            @RequestBody SellerPatchRequestDto patchRequestDto
            ) {
        Optional<SellerEntity> sellerEntity = sellerService.update(id, patchRequestDto);
        if (sellerEntity.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(sellerMapper.map(sellerEntity.get()));
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable String id) {
        boolean result = sellerService.delete(id);
        if (result) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(null);
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
    }
}
