package shift_lab.crm.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shift_lab.crm.api.dto.request.transaction.TransactionCreateRequestDto;
import shift_lab.crm.api.dto.response.transaction.TransactionResponseDto;
import shift_lab.crm.api.mapper.TransactionMapper;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.service.TransactionService;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private TransactionService transactionService;
    private TransactionMapper transactionMapper;

    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(@RequestBody TransactionCreateRequestDto transactionDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionMapper.map(transactionService.create(transactionDto)));

    }

    @GetMapping("/all")
    public ResponseEntity<List<TransactionResponseDto>> getAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionService.findAll(page, size).stream()
                        .map(t -> transactionMapper.map(t))
                        .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getById(@PathVariable(name = "id") Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionMapper.map(transactionService.findById(id)));
    }

    @GetMapping("/seller-id/{sellerId}")
    public ResponseEntity<List<TransactionResponseDto>> getAllBySellerId(@PathVariable(name = "sellerId") Long sellerId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionService.findAllBySellerId(sellerId).stream()
                        .map(t -> transactionMapper.map(t))
                        .toList());
    }
}
