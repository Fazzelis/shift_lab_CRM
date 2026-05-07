package shift_lab.crm.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import shift_lab.crm.api.dto.response.ErrorResponse;
import shift_lab.crm.core.exception.BusinessException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex, WebRequest request) {
        log.warn("Произошло исключение: код: {}, сообщение: {}", ex.getErrorCode().getStatus(), ex.getMessage());

        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(ErrorResponse.builder()
                        .title(ex.getErrorCode().getMessage())
                        .instance(request.getDescription(false).replace("uri=", ""))
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
