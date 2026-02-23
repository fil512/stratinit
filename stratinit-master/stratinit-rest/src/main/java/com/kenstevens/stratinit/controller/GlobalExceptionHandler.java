package com.kenstevens.stratinit.controller;

import com.kenstevens.stratinit.client.util.StackTraceHelper;
import com.kenstevens.stratinit.remote.exception.CommandFailedException;
import com.kenstevens.stratinit.remote.exception.StratInitException;
import com.kenstevens.stratinit.server.rest.mail.SMTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SMTPService smtpService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        String message = String.join("; ", errors);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(message, errors));
    }

    @ExceptionHandler(CommandFailedException.class)
    public ResponseEntity<ErrorResponse> handleCommandFailed(CommandFailedException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ex.getMessage(), ex.getMessages()));
    }

    @ExceptionHandler(StratInitException.class)
    public ResponseEntity<ErrorResponse> handleBusinessError(StratInitException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ex.getMessage(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        String message = ex.getMessage();
        if (message == null) {
            message = ex.getClass().getName();
        }
        logger.error(message, ex);
        smtpService.sendException("Stratinit Exception", StackTraceHelper.getStackTrace(ex));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(message, List.of(message)));
    }
}
