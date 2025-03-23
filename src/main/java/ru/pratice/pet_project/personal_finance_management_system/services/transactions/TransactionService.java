package ru.pratice.pet_project.personal_finance_management_system.services.transactions;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.Category;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.TransactionRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.categories.CategoryService;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidRequestException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;
import ru.pratice.pet_project.personal_finance_management_system.services.users.UserService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Transaction with id: " + id + " not found"));
    }

    public List<Transaction> getTransactionsByUsername(String username) {
        return transactionRepository.findTransactionsByUsername(username);
    }

    public List<Transaction> getTransactionsByCategory(Long categoryId, String username, String type) {
        checkTypeForCorrectness(type);
        Category category = categoryService.getCategoryById(categoryId);
        return transactionRepository.findTransactionByCategoryId(category, username, type);
    }

    public List<Transaction> getTransactionsByDate(LocalDate date) {
        return transactionRepository.findTransactionByDate(date);
    }

    public List<Transaction> getTransactionsByDate(String username, String type, LocalDate date) {
        checkTypeForCorrectness(type);
        return transactionRepository.findTransactionByDate(username, type, date);
    }

    public List<Transaction> getFilteredByAmountTransactions(String type, String username,
                                                             Long minAmount, Long maxAmount) {
        checkTypeForCorrectness(type);
        long finalMinAmount = minAmount == null ? 0L : minAmount;
        if (finalMinAmount >= maxAmount) {
            throw new InvalidRequestException("The minimum amount should be " +
                    "less than the maximum amount");
        }
        return transactionRepository.findTransactionsByType(username, type).stream().filter(transaction ->
                transaction.getAmount() >= finalMinAmount &&
                        transaction.getAmount() <= maxAmount).toList();
    }

    public List<Transaction> getFilteredByMonthsTransactions(String type,
                                                             String name,
                                                             Short minMonth,
                                                             Short maxMonth,
                                                             Integer year) {
        checkTypeForCorrectness(type);
        Short finalMinMonth = minMonth == null ? 1 : minMonth;
        Short finalMaxMonth = maxMonth == null ? 12 : maxMonth;
        checkMonthAndYearsForCorrectness(minMonth, maxMonth, year);
        return transactionRepository.findTransactionsByType(name, type).stream().filter(transaction ->
                transaction.getDate().getYear() == year &&
                        transaction.getDate().getMonth().getValue() >= finalMinMonth &&
                        transaction.getDate().getMonth().getValue() <= finalMaxMonth).toList();
    }

    private void checkMonthAndYearsForCorrectness(Short minMonth, Short maxMonth, Integer year) {
        if (year < 1950 || year > LocalDate.now().getYear()) {
            throw new InvalidRequestException("The year must be between 1950 and current year");
        }
        if (minMonth >= maxMonth) {
            throw new InvalidRequestException("The minimum month should be " +
                    "less than the maximum month");
        }
        if (minMonth < 0 && maxMonth > 13) {
            throw new InvalidRequestException("There are no such months");
        }
    }

    public List<Transaction> getFilteredByMonthsTransactions(String type,
                                                             String username,
                                                             Short minMonth,
                                                             Short maxMonth,
                                                             Integer year,
                                                             Long categoryId) {
        checkTypeForCorrectness(type);
        Short finalMinMonth = minMonth == null ? 1 : minMonth;
        Short finalMaxMonth = maxMonth == null ? 12 : maxMonth;
        checkMonthAndYearsForCorrectness(minMonth, maxMonth, year);
        Category category = categoryService.getCategoryById(categoryId);
        return transactionRepository.findTransactionByCategoryId(category, username, type).stream()
                .filter(transaction ->
                        transaction.getDate().getYear() == year &&
                                transaction.getDate().getMonth().getValue() >= finalMinMonth &&
                                transaction.getDate().getMonth().getValue() <= finalMaxMonth).toList();
    }

    public List<Transaction> getTransactionsByType(String type, String username) {
        checkTypeForCorrectness(type);
        return transactionRepository.findTransactionsByType(username, type);
    }

    public List<Transaction> getTransactionsByCategoryAndDate(String username, String type,
                                                              LocalDate date, Long categoryId) {
        checkTypeForCorrectness(type);
        Category category = categoryService.getCategoryById(categoryId);
        return transactionRepository.findTransactionByCategoryIdAndDate(username, type, date, category);
    }

    public List<Transaction> getSortedByAmountTransactions(String username, String type, Boolean isIncreasedSort) {
        isIncreasedSort = isIncreasedSort == null || isIncreasedSort;
        List<Transaction> transactions = getTransactionsByType(username, type);
        if (isIncreasedSort) {
            return sortTransactionsByIncreasingAmount(transactions).collect(Collectors.toList());
        }
        return sortTransactionsByDecreasingAmount(transactions).collect(Collectors.toList());
    }

    private Stream<Transaction> sortTransactionsByIncreasingAmount(List<Transaction> transactions) {
        return transactions.stream().sorted(Comparator.comparing(Transaction::getAmount));
    }

    private Stream<Transaction> sortTransactionsByDecreasingAmount(List<Transaction> transactions) {
        return transactions.stream().sorted(Comparator.comparing(Transaction::getAmount).reversed());
    }

    public List<Transaction> getLimitedNumberOfTransactions(String username,
                                                            String type,
                                                            Integer lowLimit,
                                                            Integer highLimit) {
        checkLimitsForCorrectness(lowLimit, highLimit);
        return getTransactionsByType(type, username).stream().skip(lowLimit).limit(highLimit).toList();
    }

    private void checkLimitsForCorrectness(Integer lowLimit, Integer highLimit) {
        if (lowLimit <= 0 || highLimit <= 0 || lowLimit >= highLimit) {
            throw new InvalidRequestException("The limits are specified incorrectly");
        }
    }

    public void deleteTransactionById(Long id) {
        isTransactionExistsById(id);
        log.info("Deleting transaction with id: {}", id);
        transactionRepository.deleteById(id);

    }

    public void deleteTransactionsByUsername(String username) {
        transactionRepository.deleteTransactionByUsername(username);
        log.info("Deleting transactions by username: {}", username);
    }

    public void deleteTransactionsByType(String type, String username) {
        checkTypeForCorrectness(type);
        transactionRepository.deleteTransactionByType(type, username);
        log.info("Deleting transactions by type: {} and username: {}", type, username);
    }

    public void saveTransaction(Transaction transaction) throws NullPointerException {
        checkAmountAndTypeForCorrectness(transaction);
        isThereCategoryInDatabase(transaction.getCategory().getCategoryId());
        transaction.setType(transaction.getType().trim());
        transaction.setUsername(transaction.getUsername().trim());
        checkExistenceOfUserByUsername(transaction.getUsername());
        transactionRepository.save(transaction);
        log.info("Saving transaction: {}", transaction);
    }

    @Transactional
    public void updateTransaction(Long id, Transaction transaction) {
        isThereCategoryInDatabase(transaction.getCategory().getCategoryId());
        checkAmountAndTypeForCorrectness(transaction);
        checkExistenceOfUserByUsername(transaction.getUsername());
        if (transactionRepository.existsById(id)) {
            transactionRepository.update(
                    id,
                    transaction.getDate(),
                    transaction.getDescription(),
                    transaction.getUsername().trim(),
                    transaction.getType().trim(),
                    transaction.getAmount(),
                    transaction.getCategory().getCategoryId());
            log.info("Updating transaction {} with id: {}", id, transaction);
            return;
        }
        transactionRepository.save(transaction);
        log.info("Create transaction {}", transaction);
    }

    private void checkExistenceOfUserByUsername(String username) {
        userService.getUserByName(username);
    }

    @Transactional
    public void updateAmount(Long id, Long amount) {
        isTransactionExistsById(id);
        if (amount <= 0) {
            throw new InvalidRequestException("Amount cannot be negative");
        }
        transactionRepository.updateAmount(id, amount);
        log.info("Updating the transaction amount with id: {}", id);
    }

    @Transactional
    public void updateCategory(Long id, Long categoryId) {
        isTransactionExistsById(id);
        isThereCategoryInDatabase(categoryId);
        transactionRepository.updateCategoryId(id, categoryId);
        log.info("Updating the transaction category with id: {}", id);
    }

    @Transactional
    public void updateDate(Long id, LocalDate date) {
        isTransactionExistsById(id);
        transactionRepository.updateDate(id, date);
        log.info("Updating the transaction date with id: {}", id);
    }

    @Transactional
    public void updateDescription(Long id, String description) {
        isTransactionExistsById(id);
        transactionRepository.updateDescription(id, description.trim());
        log.info("Updating the transaction description with id: {}", id);
    }

    private void isTransactionExistsById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found");
        }
    }

    private void isThereCategoryInDatabase(Long categoryId) {
        if (!categoryService.isCategoryExistsByCategoryId(categoryId)) {
            throw new InvalidEntityException("The category does not exist");
        }
    }

    private void checkAmountAndTypeForCorrectness(Transaction transaction) {
        if (transaction.getAmount() <= 0 ||
                (!transaction.getType().trim().equals("доход") && !transaction.getType().trim().equals("расход"))) {
            throw new InvalidEntityException("Entity not correct");
        }
    }

    private void checkTypeForCorrectness(String type) {
        if (!type.trim().equals("доход") && !type.trim().equals("расход")) {
            throw new InvalidRequestException("The type should be 'доход' or 'расход'");
        }
    }
}