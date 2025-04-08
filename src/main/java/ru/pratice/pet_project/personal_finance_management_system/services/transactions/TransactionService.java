package ru.pratice.pet_project.personal_finance_management_system.services.transactions;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.Category;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.TransactionRepository;
import ru.pratice.pet_project.personal_finance_management_system.repositories.users.User;
import ru.pratice.pet_project.personal_finance_management_system.services.categories.CategoryService;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidRequestException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;
import ru.pratice.pet_project.personal_finance_management_system.services.limits.LimitService;
import ru.pratice.pet_project.personal_finance_management_system.services.users.UserService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionService {
    static String INCOME_TYPE = "доход";
    static String CONSUMPTION_TYPE = "расход";
    TransactionRepository transactionRepository;
    CategoryService categoryService;
    UserService userService;
    LimitService limitService;

    public Transaction getTransactionById(long id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Transaction with id: " + id + " not found"));
    }

    public List<Transaction> getTransactionsByUsername(String username) {
        return transactionRepository.findTransactionsByUsername(username);
    }

    public List<Transaction> getTransactionsByCategory(long categoryId, String username, String type) {
        checkTypeForCorrectness(type);
        Category category = categoryService.getCategoryById(categoryId);
        return transactionRepository.findTransactionByCategoryId(category, username, type);
    }

    public List<Transaction> getTransactionsByDate(String username, String type, LocalDate date) {
        checkTypeForCorrectness(type);
        return transactionRepository.findTransactionByDate(username, type, date);
    }

    public List<Transaction> getFilteredByAmountTransactions(String type, String username,
                                                             long minAmount, long maxAmount) {
        checkTypeForCorrectness(type);
        if (minAmount >= maxAmount) {
            throw new InvalidRequestException("The minimum amount should be " +
                    "less than the maximum amount");
        }
        return transactionRepository.findTransactionsByType(username, type).stream().filter(transaction ->
                transaction.getAmount() >= minAmount &&
                        transaction.getAmount() <= maxAmount).toList();
    }

    public List<Transaction> getFilteredByMonthsTransactions(String type,
                                                             String name,
                                                             short minMonth,
                                                             short maxMonth,
                                                             int year) {
        checkTypeForCorrectness(type);
        short finalMinMonth = minMonth < 1 ? 1 : minMonth;
        short finalMaxMonth = maxMonth == 0 || maxMonth > 12 ? 12 : maxMonth;
        checkMonthAndYearsForCorrectness(minMonth, maxMonth, year);
        return transactionRepository.findTransactionsByType(name, type).stream().filter(transaction ->
                transaction.getDate().getYear() == year &&
                        transaction.getDate().getMonth().getValue() >= finalMinMonth &&
                        transaction.getDate().getMonth().getValue() <= finalMaxMonth).toList();
    }

    public List<Transaction> getFilteredByMonthsTransactions(String type,
                                                             String username,
                                                             short minMonth,
                                                             short maxMonth,
                                                             int year,
                                                             long categoryId) {
        checkTypeForCorrectness(type);
        short finalMinMonth = minMonth < 1 ? 1 : minMonth;
        short finalMaxMonth = maxMonth == 0 || maxMonth > 12 ? 12 : maxMonth;
        checkMonthAndYearsForCorrectness(minMonth, maxMonth, year);
        Category category = categoryService.getCategoryById(categoryId);
        return transactionRepository.findTransactionByCategoryId(category, username, type).stream()
                .filter(transaction ->
                        transaction.getDate().getYear() == year &&
                                transaction.getDate().getMonth().getValue() >= finalMinMonth &&
                                transaction.getDate().getMonth().getValue() <= finalMaxMonth).toList();
    }

    private void checkMonthAndYearsForCorrectness(short minMonth, short maxMonth, int year) {
        if (year < 1950 || year > LocalDate.now().getYear()) {
            throw new InvalidRequestException("The year must be between 1950 and current year");
        }
        if (minMonth >= maxMonth) {
            throw new InvalidRequestException("The minimum month should be " +
                    "less than the maximum month");
        }
    }

    public List<Transaction> getTransactionsByType(String type, String username) {
        checkTypeForCorrectness(type);
        return transactionRepository.findTransactionsByType(username, type);
    }

    public List<Transaction> getTransactionsByCategoryAndDate(String username, String type,
                                                              LocalDate date, long categoryId) {
        checkTypeForCorrectness(type);
        Category category = categoryService.getCategoryById(categoryId);
        return transactionRepository.findTransactionByCategoryIdAndDate(username, type, date, category);
    }

    public List<Transaction> getSortedByAmountTransactions(String username, String type, boolean isIncreasedSort) {
        List<Transaction> transactions = getTransactionsByType(username, type);
        if (isIncreasedSort) {
            return sortTransactionsByIncreasingAmount(transactions).toList();
        }
        return sortTransactionsByDecreasingAmount(transactions).toList();
    }

    private Stream<Transaction> sortTransactionsByIncreasingAmount(List<Transaction> transactions) {
        return transactions.stream().sorted(Comparator.comparing(Transaction::getAmount));
    }

    private Stream<Transaction> sortTransactionsByDecreasingAmount(List<Transaction> transactions) {
        return transactions.stream().sorted(Comparator.comparing(Transaction::getAmount).reversed());
    }

    public List<Transaction> getLimitedNumberOfTransactions(String username, String type,
                                                            int lowLimit, int highLimit) {
        checkLimitsForCorrectness(lowLimit, highLimit);
        return getTransactionsByType(type, username).stream().skip(lowLimit).limit(highLimit).toList();
    }

    private void checkLimitsForCorrectness(int lowLimit, int highLimit) {
        if (lowLimit <= 0 || highLimit <= 0 || lowLimit >= highLimit) {
            throw new InvalidRequestException("The limits are specified incorrectly");
        }
    }

    public void deleteTransactionById(long id) {
        isTransactionExistsById(id);
        log.info("Deleting transaction with id: {}", id);
        transactionRepository.deleteById(id);
        updateAmountOfExpenses(id, getAmountById(id), false);
    }

    public void deleteTransactionsByUsername(String username) {
        long amount = receivesAmountFromTransactionList(getTransactionsByUsername(username));
        transactionRepository.deleteTransactionByUsername(username);
        log.info("Deleting transactions by username: {}", username);
        updateTotalAmountAfterDeleteByUsername(username, amount);
    }

    public void deleteTransactionsByType(String type, String username) {
        long amount = receivesAmountFromTransactionList(getTransactionsByType(type, username));
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

    @Transactional
    public void saveTransaction(Transaction transaction) {
        trimTypeAndUsername(transaction);
        checkTransaction(transaction);
        transactionRepository.save(transaction);
        log.info("Saving transaction: {}", transaction);
        updateTotalAmountAfterSave(transaction);
    }

    private void updateTotalAmountAfterSave(Transaction transaction) {
        if (transaction.getType().equals(CONSUMPTION_TYPE)) {
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
                    transaction.getCategory().getCategoryId());
            log.info("Updating transaction {} with id: {}", id, transaction);
            updateAmountOfExpenses(id, transaction.getAmount(), true);
            return;
        }
        saveTransaction(transaction);
        log.info("Create transaction {}", transaction);
    }

    private void checkTransaction(Transaction transaction) {
        checkTypeForCorrectness(transaction.getType());
        checkAmountForCorrectness(transaction.getAmount());
        isThereCategoryInDatabase(transaction.getCategory().getCategoryId());
        checkExistenceOfUserByUsername(transaction.getUsername());
    }

    private void checkExistenceOfUserByUsername(String username) {
        userService.getUserByName(username);
    }

    private void checkAmountForCorrectness(long amount) {
        if (amount <= 0) {
            throw new InvalidEntityException("Amount must be greater than zero");
        }
    }

    private void checkTypeForCorrectness(String type) {
        if (!type.trim().equals(INCOME_TYPE) && !type.trim().equals(CONSUMPTION_TYPE)) {
            throw new InvalidRequestException("The type should be '" + INCOME_TYPE + "' or '" + CONSUMPTION_TYPE + "'");
        }
    }

    @Transactional
    public void updateAmount(long id, long amount) {
        isTransactionExistsById(id);
        checkAmountForCorrectness(amount);
        transactionRepository.updateAmount(id, amount);
        log.info("Updating the transaction amount with id: {}", id);
        updateAmountOfExpenses(id, amount, true);
    }

    private void updateAmountOfExpenses(long id, long amount, boolean isUpdateMethod) {
        if (isTypeConsumption(id))
            updateTotalAmountInLimitTable(id, amount, isUpdateMethod);
    }

    private boolean isTypeConsumption(long id) {
        return getTransactionById(id).getType().equals(CONSUMPTION_TYPE);
    }

    private void updateTotalAmountInLimitTable(long id, long amount, boolean isUpdateMethod) {
        User user = userService.getUserByName(getTransactionById(id).getUsername());
        if (isUpdateMethod) {
            limitService.updateTotalAmount(user, getAmountById(id), amount);
        } else
            limitService.updateTotalAmount(user, 0L, amount);
    }

    private long getAmountById(long id) {
        return getTransactionById(id).getAmount();
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

    private void isTransactionExistsById(long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction with id: " + id + " not found");
        }
    }

    private void isThereCategoryInDatabase(long categoryId) {
        if (!categoryService.isCategoryExistsByCategoryId(categoryId)) {
            throw new InvalidEntityException("The category does not exist");
        }
    }

}