package ru.pratice.pet_project.personal_finance_management_system.services.transactions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TypeOfTransaction {
    INCOME_TYPE("доход"),
    CONSUMPTION_TYPE("расход");

    String name;
}
