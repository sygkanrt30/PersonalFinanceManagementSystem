package ru.pratice.pet_project.personal_finance_management_system.services.transactions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.TransactionRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;
import ru.pratice.pet_project.personal_finance_management_system.services.limits.LimitService;
import ru.pratice.pet_project.personal_finance_management_system.services.users.UserService;

import java.util.List;

import static ru.pratice.pet_project.personal_finance_management_system.services.transactions.TransactionGetService.CONSUMPTION_TYPE;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionDeleteService {
    LimitService limitService;
    TransactionGetService transactionGetService;
    TransactionSaveAndUpdateService transactionService;
    TransactionRepository transactionRepository;
    UserService userService;

    public void deleteTransactionById(long id) {
        if (transactionRepository.existsById(id)) {
            log.info("Deleting transaction with id: {}", id);
            transactionRepository.deleteById(id);
            long amount = transactionGetService.getTransactionById(id).getAmount();
            transactionService.updateAmountOfExpenses(id, amount, false);
        } else
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found");

    }

    public void deleteTransactionsByUsername(String username) {
        long amount = receivesAmountFromTransactionList(transactionGetService.getTransactionsByUsername(username));
        transactionRepository.deleteTransactionByUsername(username);
        log.info("Deleting transactions by username: {}", username);
        updateTotalAmountAfterDeleteByUsername(username, amount);
    }

    public void deleteTransactionsByType(String type, String username) {
        long amount = receivesAmountFromTransactionList(transactionGetService.getTransactionsByType(type, username));
        transactionRepository.deleteTransactionByType(type, username);
        log.info("Deleting transactions by type: {} and username: {}", type, username);
        updateTotalAmountAfterDeleteByUsername(username, amount);
    }

    private void updateTotalAmountAfterDeleteByUsername(String username, long amount) {
        limitService.updateTotalAmount(userService.getUserByName(username), 0L, amount);
    }

    private long receivesAmountFromTransactionList(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getType().equals(CONSUMPTION_TYPE))
                .map(Transaction::getAmount)
                .reduce(0L, Long::sum);
    }
}
