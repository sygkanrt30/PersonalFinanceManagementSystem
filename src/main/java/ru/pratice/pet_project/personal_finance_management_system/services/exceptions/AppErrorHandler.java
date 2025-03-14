package ru.pratice.pet_project.personal_finance_management_system.services.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
public class AppErrorHandler {
    private int statusCode;
    private String message;
    private LocalTime time;
}