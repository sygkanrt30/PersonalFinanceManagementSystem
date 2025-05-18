package ru.pratice.pet_project.personal_finance_management_system.services.transactions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.TransactionRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;
import ru.pratice.pet_project.personal_finance_management_system.services.limits.LimitService;
import ru.pratice.pet_project.personal_finance_management_system.services.users.UserService;


@Slf4j
@Service
@AllArgsConstructor
public class TransactionDeleteService {
    LimitService limitService;
    TransactionGetService transactionGetService;
    TransactionSaveAndUpdateService transactionSaveAndUpdateService;
    TransactionRepository transactionRepository;
    UserService userService;

    public void deleteTransactionById(long id) {
        if (transactionRepository.existsById(id)) {
            log.info("Deleting transaction with id: {}", id);
            transactionRepository.deleteById(id);
            long amount = transactionGetService.getTransactionById(id).getAmount();
            transactionSaveAndUpdateService.updateAmountOfExpenses(id, amount, false);
        } else
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found");
    }

    public void deleteTransactionsByUsername(String username) {
        long amount = transactionRepository.sumOfTransactionsAmountWithTypeConsumption(username,
                TypeOfTransaction.CONSUMPTION_TYPE.name());
        transactionRepository.deleteTransactionByUsername(username);
        updateTotalAmountAfterDeleteByUsername(username, amount);
    }

    public void deleteTransactionsByType(String type, String username) {
        long amount = transactionRepository.sumOfTransactionsAmountWithTypeConsumption(username,
                TypeOfTransaction.CONSUMPTION_TYPE.name());
        transactionRepository.deleteTransactionByType(type, username);
        updateTotalAmountAfterDeleteByUsername(username, amount);
    }

    private void updateTotalAmountAfterDeleteByUsername(String username, long amount) {
        log.info("Deleting transactions by username: {}", username);
        limitService.updateTotalAmount(userService.getUserByName(username), 0L, amount);
    }
}
