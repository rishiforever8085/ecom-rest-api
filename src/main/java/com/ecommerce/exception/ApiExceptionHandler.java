package com.ecommerce.exception;

import com.ecommerce.ResponseWithStatus;
import com.ecommerce.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.ecommerce.util.Constant.CORRELATION_ID;

@ControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity.ok(
                new ResponseWithStatus(new Status(false, ex.getMessage(), MDC.get(CORRELATION_ID)), null));
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity handleException(DatabaseException ex) {
        ex.printStackTrace();
        return ResponseEntity.ok(new ResponseWithStatus(new Status(false, ex.getMessage(), MDC.get(CORRELATION_ID)), null));
    }
}
