package com.cristovantamayo.order.config;

import com.cristovantamayo.order.exception.DuplicateRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrity(DataIntegrityViolationException ex) {
        String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";

        if (rootMsg.contains("uq_orders_idempotence_key") || rootMsg.contains("ORA-00001")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // HTTP 409 Conflict
                    .body("Duplicate request detected. This order is already being processed.");
        }

        return ResponseEntity // Fallback
                .status(HttpStatus.BAD_REQUEST)
                .body("Database integrity violation occurred.");
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateRequest(DuplicateRequestException ex) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", HttpStatus.CONFLICT.value(),               // 409 Conflict
                "error", "Conflict",
                "message", ex.getMessage(),                          // "Idempotency key already processed"
                "path", "/orders"
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, String>> handleMissingHeader(MissingRequestHeaderException ex) {
        if ("Idempotency-Key".equals(ex.getHeaderName())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "error", "Bad Request",
                            "message", "The HTTP header 'Idempotency-Key' is required for this endpoint."
                    ));
        }

        // Generic fallback for any other missing header
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Missing Header", "message", ex.getMessage()));
    }
}
