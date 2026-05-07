package shift_lab.crm.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shift_lab.crm.api.dto.request.transaction.TransactionCreateRequestDto;
import shift_lab.crm.api.dto.response.transaction.TransactionResponseDto;
import shift_lab.crm.api.mapper.TransactionMapper;
import shift_lab.crm.core.entity.TransactionEntity;
import shift_lab.crm.core.service.TransactionService;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private TransactionService transactionService;
    private TransactionMapper transactionMapper;

    @PostMapping
    public ResponseEntity<TransactionResponseDto> create(@RequestBody TransactionCreateRequestDto transactionDto) {
        Optional<TransactionEntity> optionalTransaction = transactionService.create(transactionDto);
        if (optionalTransaction.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(transactionMapper.map(optionalTransaction.get()));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(null);
    }
}
