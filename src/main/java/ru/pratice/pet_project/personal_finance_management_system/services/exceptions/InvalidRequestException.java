package ru.pratice.pet_project.personal_finance_management_system.services.exceptions;

import lombok.Getter;

@Getter
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
