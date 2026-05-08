package shift_lab.crm.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shift_lab.crm.api.dto.request.transaction.TransactionCreateRequestDto;
import shift_lab.crm.api.dto.response.transaction.TransactionResponseDto;
import shift_lab.crm.api.dto.response.transaction.TransactionsResponseDto;
import shift_lab.crm.api.mapper.TransactionMapper;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.service.TransactionService;

import java.util.List;

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
    public ResponseEntity<TransactionsResponseDto> getAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<TransactionEntity> transactions = transactionService.findAll(page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TransactionsResponseDto.builder()
                        .transactions(transactions.stream()
                                .map(t -> transactionMapper.map(t))
                                .toList())
                        .totalPages(transactions.getTotalPages())
                        .totalElements(transactions.getTotalElements())
                        .hasNext(transactions.hasNext())
                        .hasPrevious(transactions.hasPrevious())
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getById(@PathVariable(name = "id") Long id){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionMapper.map(transactionService.findById(id)));
    }

    @GetMapping("/seller-id/{sellerId}")
    public ResponseEntity<TransactionsResponseDto> getAllBySellerId(
            @PathVariable(name = "sellerId") Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<TransactionEntity> transactions = transactionService.findAllBySellerId(sellerId, page, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TransactionsResponseDto.builder()
                        .transactions(transactions.stream()
                                .map(t -> transactionMapper.map(t))
                                .toList())
                        .totalPages(transactions.getTotalPages())
                        .totalElements(transactions.getTotalElements())
                        .hasNext(transactions.hasNext())
                        .hasPrevious(transactions.hasPrevious())
                        .build());
    }
}
