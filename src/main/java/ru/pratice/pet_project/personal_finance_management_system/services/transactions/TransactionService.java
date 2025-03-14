package ru.pratice.pet_project.personal_finance_management_system.services.transactions;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.Category;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.CategoryRepository;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.TransactionRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.categories.CategoryService;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidRequestException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Transaction with id: " + id + " not found", LocalTime.now()));
    }

    public List<Transaction> getTransactionsByOwner(String owner) {
        return transactionRepository.findTransactionsByOwnerName(owner);
    }

    public List<Transaction> getTransactionsByType(String type) {
        return transactionRepository.findTransactionByType(type);
    }

    public List<Transaction> getTransactionsByCategory(Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        return transactionRepository.findTransactionByCategoryId(category);
    }

    public List<Transaction> getTransactionsByDate(LocalDate date) {
        return transactionRepository.findTransactionByDate(date);
    }

    public List<Transaction> getFilteredByAmountTransactions(String type, String name,  Long minAmount, Long maxAmount) {
        isTypeCorrect(type);
        minAmount = minAmount == null ? 0 : minAmount;
        if (minAmount >= maxAmount) {
            throw new InvalidRequestException("The minimum amount should be " +
                    "less than the maximum amount", LocalTime.now());
        }
        Long finalMinAmount = minAmount;
        return transactionRepository.findTransactionByType(type).stream().filter(transaction ->
                transaction.getAmount() >= finalMinAmount &&
                        transaction.getAmount() <= maxAmount &&
                        transaction.getOwner().equals(name.trim())).toList();
    }

    public List<Transaction> getFilteredByMonthsTransactions(String type,
                                                             String name,
                                                             Short minMonth,
                                                             Short maxMonth,
                                                             Integer year) {
        minMonth = minMonth == null ? 1 : minMonth;
        maxMonth = maxMonth == null ? 12 : maxMonth;
        isTypeCorrect(type);
        if (year < 1950 || year > LocalDate.now().getYear()) {
            throw new InvalidRequestException("The year must be between 1950 and current year", LocalTime.now());
        }
        if (minMonth >= maxMonth) {
            throw new InvalidRequestException("The minimum month should be " +
                    "less than the maximum month", LocalTime.now());
        }
        if (!(minMonth > 0 && maxMonth < 13)) {
            throw new InvalidRequestException("There are no such months", LocalTime.now());
        }

        Short finalMinMonth = minMonth;
        Short finalMaxMonth = maxMonth;
        return transactionRepository.findTransactionByType(type.trim()).stream().filter(transaction ->
                transaction.getDate().getYear() == year &&
                        transaction.getDate().getMonth().getValue() >= finalMinMonth &&
                        transaction.getDate().getMonth().getValue() <= finalMaxMonth &&
                        transaction.getOwner().equals(name.trim())).toList();
    }

    public void deleteTransactionById(Long id) {
        if (transactionRepository.existsById(id)) {
            log.info("Deleting transaction with id: {}", id);
            transactionRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found", LocalTime.now());
        }
    }

    public void deleteTransactionsByOwner(String ownerName) {
        log.info("Deleting transactions by owner: {}", ownerName);
        transactionRepository.deleteTransactionByOwner(ownerName);
    }

    public void deleteTransactionsByType(String type) {
        log.info("Deleting transactions by type: {}", type);
        transactionRepository.deleteTransactionByType(type);
    }

    public Transaction createTransaction(Transaction transaction) {
        try {
            checkAmountAndType(transaction);
            isThereCategoryInDatabase(transaction);
            transaction.setType(transaction.getType().trim());
            transaction.setOwner(transaction.getOwner().trim());
            log.info("Saving transaction: {}", transaction);
            return transactionRepository.save(transaction);
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new InvalidEntityException("Transaction can't be null", LocalTime.now());
        }
    }

    @Transactional
    public void updateTransaction(Long id, Transaction transaction) {
        isThereCategoryInDatabase(transaction);
        if (transactionRepository.existsById(id)) {
            try {
                checkAmountAndType(transaction);
                transactionRepository.update(
                        id,
                        transaction.getDate(),
                        transaction.getDescription(),
                        transaction.getOwner().trim(),
                        transaction.getType().trim(),
                        transaction.getAmount(),
                        transaction.getCategory().getCategoryId());
            } catch (NullPointerException e) {
                log.error(e.getMessage(), e);
                throw new InvalidEntityException("Transaction can't be null", LocalTime.now());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new InvalidEntityException(e.getMessage(), LocalTime.now());
            }
            log.info("Updating transaction {} with id: {}", id, transaction);
        } else {
            try {
                transactionRepository.save(transaction);
            } catch (NullPointerException e) {
                log.error(e.getMessage(), e);
                throw new InvalidEntityException("Transaction can't be null", LocalTime.now());
            }
            log.info("Create transaction {}", transaction);
        }
    }

    @Transactional
    public void updateAmount(Long id, Long amount) {
        if (transactionRepository.existsById(id) && amount > 0) {
            transactionRepository.updateAmount(id, amount);
            log.info("Updating the transaction amount with id: {}", id);
        } else if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found", LocalTime.now());
        }
    }

    @Transactional
    public void updateCategory(Long id, Long categoryId) {
        if (transactionRepository.existsById(id) && categoryRepository.findByCategoryId(categoryId).isPresent()) {
            transactionRepository.updateCategoryId(id, categoryId);
            log.info("Updating the transaction category with id: {}", id);
        } else if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found", LocalTime.now());
        } else {
            throw new InvalidEntityException("The category does not exist", LocalTime.now());
        }
    }

    @Transactional
    public void updateDate(Long id, String date) {
        if (transactionRepository.existsById(id)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate;
            try {
                localDate = LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                throw new InvalidEntityException("User birth date is incorrect", LocalTime.now());
            }
            transactionRepository.updateDate(id, localDate);
            log.info("Updating the transaction date with id: {}", id);
        } else {
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found", LocalTime.now());
        }
    }

    @Transactional
    public void updateDescription(Long id, String description) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.updateDescription(id, description.trim());
            log.info("Updating the transaction description with id: {}", id);
        } else {
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found", LocalTime.now());
        }
    }

    private void isThereCategoryInDatabase(Transaction transaction) {
        if (!categoryService.getCategoryByCategoryId(transaction.getCategory())) {
            throw new InvalidEntityException("The category does not exist", LocalTime.now());
        }
    }

    private void checkAmountAndType(Transaction transaction) {
        if (transaction.getAmount() <= 0 ||
                (!transaction.getType().trim().equals("доход") && !transaction.getType().trim().equals("расход"))) {
            throw new InvalidEntityException("Entity not correct", LocalTime.now());
        }
    }

    private void isTypeCorrect(String type) {
        if (!type.trim().equals("доход") && !type.trim().equals("расход")) {
            throw new InvalidRequestException("The type should be 'доход' or 'расход'", LocalTime.now());
        }
    }
}








































