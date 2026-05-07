package shift_lab.crm.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    SELLER_NOT_FOUND(HttpStatus.NOT_FOUND, "Продавец не найден"),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Транзакция не найдена"),
    NUMBER_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST, "Неверный формат числа");

    private final HttpStatus status;
    private final String message;
}
