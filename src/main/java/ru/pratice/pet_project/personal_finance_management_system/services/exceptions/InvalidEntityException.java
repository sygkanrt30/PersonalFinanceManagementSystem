package ru.pratice.pet_project.personal_finance_management_system.services.exceptions;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class InvalidEntityException extends RuntimeException {
    private final LocalTime time;

    public InvalidEntityException(String message, LocalTime time) {
        super(message);
        this.time = time;
    }
}
