package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidRequestException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionController {
    @ExceptionHandler
    public ResponseEntity<String> catchResourceNotFoundException(ResourceNotFoundException e) {
        return getAppErrorHandlerResponseEntity(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidRequestException.class, InvalidRequestException.class})
    public ResponseEntity<String> catchInvalidEntityException(Exception e) {
        return getAppErrorHandlerResponseEntity(e, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler
    public ResponseEntity<String> catchBadRequestException(MissingServletRequestParameterException e) {
        return getAppErrorHandlerResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NullPointerException.class, IllegalStateException.class})
    public ResponseEntity<String> catchNullPointerException(Exception e) {
        return getAppErrorHandlerResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> getAppErrorHandlerResponseEntity(Exception e, HttpStatus status) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(status).body(e.getMessage());
    }
}
