package ru.pratice.pet_project.personal_finance_management_system.services.transactions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.Category;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.repositories.transactions.TransactionRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.categories.CategoryService;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidRequestException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionGetService {
    static String INCOME_TYPE = "доход";
    public static String CONSUMPTION_TYPE = "расход";
    TransactionRepository transactionRepository;
    CategoryService categoryService;

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

    public void checkTypeForCorrectness(String type) {
        if (!type.trim().equals(INCOME_TYPE) && !type.trim().equals(CONSUMPTION_TYPE)) {
            throw new InvalidRequestException("The type should be '" + INCOME_TYPE + "' or '" + CONSUMPTION_TYPE + "'");
        }
    }
}
