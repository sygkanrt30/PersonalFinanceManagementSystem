package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.*;

@ControllerAdvice
@Log4j2
public class GlobalExceptionController {
    @ExceptionHandler
    public ResponseEntity<AppErrorHandler> catchResourceNotFoundException(ResourceNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppErrorHandler(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage(),
                e.getTime()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<AppErrorHandler> catchEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppErrorHandler(HttpStatus.CONFLICT.value(),
                e.getMessage(),
                e.getTime()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<AppErrorHandler> catchInvalidEntityException(InvalidEntityException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppErrorHandler(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                e.getTime()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<AppErrorHandler> catchInvalidRequestException(InvalidRequestException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppErrorHandler(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                e.getTime()),
                HttpStatus.BAD_REQUEST);
    }
}
