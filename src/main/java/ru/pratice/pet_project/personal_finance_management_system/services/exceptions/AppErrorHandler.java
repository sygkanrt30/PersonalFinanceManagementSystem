package ru.pratice.pet_project.personal_finance_management_system.services.exceptions;

import java.time.LocalTime;


public record AppErrorHandler(int statusCode, String message, LocalTime time) {}