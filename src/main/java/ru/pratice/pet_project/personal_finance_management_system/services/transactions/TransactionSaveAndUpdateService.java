package ru.pratice.pet_project.personal_finance_management_system.services.transactions;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.entities.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.repositories.TransactionRepository;
import ru.pratice.pet_project.personal_finance_management_system.entities.User;
import ru.pratice.pet_project.personal_finance_management_system.services.categories.CategoryService;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;
import ru.pratice.pet_project.personal_finance_management_system.services.limits.LimitService;
import ru.pratice.pet_project.personal_finance_management_system.services.users.UserService;

import java.time.LocalDate;


@Slf4j
@Service
@AllArgsConstructor
public class TransactionSaveAndUpdateService {
    TransactionRepository transactionRepository;
    CategoryService categoryService;
    UserService userService;
    LimitService limitService;
    TransactionGetService transactionGetService;

    @Transactional
    public void saveTransaction(Transaction transaction) {
        trimTypeAndUsername(transaction);
        checkTransaction(transaction);
        transactionRepository.save(transaction);
        log.info("Saving transaction: {}", transaction);
        updateTotalAmountAfterSave(transaction);
    }

    private void updateTotalAmountAfterSave(Transaction transaction) {
        if (transaction.getType().equals(TypeOfTransaction.CONSUMPTION_TYPE.name())) {
            User user = userService.getUserByName(transaction.getUsername());
            limitService.updateTotalAmountAfterSave(user, transaction.getAmount());
        }
    }

    private void trimTypeAndUsername(Transaction transaction) {
        transaction.setType(transaction.getType().trim());
        transaction.setUsername(transaction.getUsername().trim());
    }

    @Transactional
    public void updateTransaction(long id, Transaction transaction) {
        trimTypeAndUsername(transaction);
        checkTransaction(transaction);
        if (transactionRepository.existsById(id)) {
            transactionRepository.update(
                    id,
                    transaction.getDate(),
                    transaction.getDescription(),
                    transaction.getUsername(),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getCategory().getId());
            log.info("Updating transaction {} with id: {}", id, transaction);
            updateAmountOfExpenses(id, transaction.getAmount(), true);
            return;
        }
        saveTransaction(transaction);
        log.info("Create transaction {}", transaction);
    }

    private void checkTransaction(Transaction transaction) {
        transactionGetService.checkTypeForCorrectness(transaction.getType());
        checkAmountForCorrectness(transaction.getAmount());
        isThereCategoryInDatabase(transaction.getCategory().getId());
        checkExistenceOfUserByUsername(transaction.getUsername());
    }

    private void checkAmountForCorrectness(long amount) {
        if (amount <= 0) {
            throw new InvalidEntityException("Amount must be greater than zero");
        }
    }

    private void checkExistenceOfUserByUsername(String username) {
        userService.getUserByName(username);
    }

    @Transactional
    public void updateAmount(long id, long amount) {
        isTransactionExistsById(id);
        checkAmountForCorrectness(amount);
        transactionRepository.updateAmount(id, amount);
        log.info("Updating the transaction amount with id: {}", id);
        updateAmountOfExpenses(id, amount, true);
    }

    private void isTransactionExistsById(long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found");
        }
    }

    public void updateAmountOfExpenses(long id, long amount, boolean isUpdateMethod) {
        if (isTypeConsumption(id))
            updateTotalAmountInLimitTable(id, amount, isUpdateMethod);
    }

    private boolean isTypeConsumption(long id) {
        return transactionGetService.getTransactionById(id).getType().equals(TypeOfTransaction.CONSUMPTION_TYPE.name());
    }

    private void updateTotalAmountInLimitTable(long id, long amount, boolean isUpdateMethod) {
        User user = userService.getUserByName(transactionGetService.getTransactionById(id).getUsername());
        if (isUpdateMethod) {
            long previousAmount = transactionGetService.getTransactionById(id).getAmount();
            limitService.updateTotalAmount(user, previousAmount, amount);
        } else
            limitService.updateTotalAmount(user, 0L, amount);
    }

    @Transactional
    public void updateCategory(long id, long categoryId) {
        isTransactionExistsById(id);
        isThereCategoryInDatabase(categoryId);
        transactionRepository.updateCategoryId(id, categoryId);
        log.info("Updating the transaction category with id: {}", id);
    }

    @Transactional
    public void updateDate(long id, LocalDate date) {
        isTransactionExistsById(id);
        transactionRepository.updateDate(id, date);
        log.info("Updating the transaction date with id: {}", id);
    }

    @Transactional
    public void updateDescription(long id, String description) {
        isTransactionExistsById(id);
        transactionRepository.updateDescription(id, description.trim());
        log.info("Updating the transaction description with id: {}", id);
    }

    private void isThereCategoryInDatabase(long categoryId) {
        if (!categoryService.isCategoryExistsByCategoryId(categoryId)) {
            throw new InvalidEntityException("The category does not exist");
        }
    }
}