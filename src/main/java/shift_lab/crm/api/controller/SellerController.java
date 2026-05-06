package shift_lab.crm.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shift_lab.crm.api.dto.request.seller.SellerRequestDto;
import shift_lab.crm.api.dto.response.seller.SellerResponseDto;
import shift_lab.crm.api.mapper.SellerMapper;
import shift_lab.crm.core.entity.SellerEntity;
import shift_lab.crm.core.service.SellerService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("seller")
public class SellerController {
    private SellerService sellerService;
    private SellerMapper sellerMapper;

    @PostMapping
    public ResponseEntity<SellerResponseDto> create(@RequestBody SellerRequestDto sellerDto) {
        SellerResponseDto createdSeller = sellerMapper.map(sellerService.create(sellerDto));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdSeller);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponseDto> getSellerById(@PathVariable String id) {
        try {
            Long parsedId = Long.parseLong(id);
            Optional<SellerEntity> optionalSeller = sellerService.getById(parsedId);
            if (optionalSeller.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(null);
            }
            SellerResponseDto sellerResponse = sellerMapper.map(optionalSeller.get());
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(sellerResponse);
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<SellerResponseDto>> getAllSellers(int page,  int size) {
        List<SellerResponseDto> sellers = sellerService.getAllSellers(page, size).stream()
                .filter(seller -> !seller.getIsDeleted())
                .map(seller -> sellerMapper.map(seller))
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sellers);
    }
}
