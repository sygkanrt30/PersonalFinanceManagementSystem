package ru.pratice.pet_project.personal_finance_management_system.services.exceptions;

import lombok.Getter;

@Getter
public class InvalidEntityException extends RuntimeException {
    public InvalidEntityException(String message) {
        super(message);
    }
}
