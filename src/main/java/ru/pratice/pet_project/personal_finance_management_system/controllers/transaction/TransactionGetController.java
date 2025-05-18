package ru.pratice.pet_project.personal_finance_management_system.controllers.transaction;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.entities.Transaction;
import ru.pratice.pet_project.personal_finance_management_system.services.transactions.TransactionGetService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/transactions_get")
@AllArgsConstructor
public class TransactionGetController {
    TransactionGetService transactionGetService;

    @GetMapping(path = "/{id}")
    public Transaction getTransaction(@PathVariable(name = "id") long id) {
        return transactionGetService.getTransactionById(id);
    }

    @GetMapping(path = "/by-username/{username}")
    public List<Transaction> getFilteredTransactionsByUsername(@PathVariable(name = "username") String username) {
        return transactionGetService.getTransactionsByUsername(username.trim());
    }

    @GetMapping(path = "/by-category")
    public List<Transaction> getTransactionsByCategory(@RequestParam long categoryId,
                                                       @RequestParam String username,
                                                       @RequestParam String type) {
        return transactionGetService.getTransactionsByCategory(categoryId, username.trim(), type.trim());
    }

    @GetMapping("/filtered-by-type")
    public List<Transaction> getFilteredTransactionsByType(@RequestParam String type,
                                                           @RequestParam String username) {
        return transactionGetService.getTransactionsByType(type.trim(), username.trim());
    }

    @GetMapping("/filtered-by-month")
    public List<Transaction> getFilteredTransactionsByMonth(@RequestParam String username,
                                                            @RequestParam String type,
                                                            @RequestParam(required = false) short minMonth,
                                                            @RequestParam(required = false) short maxMonth,
                                                            @RequestParam int year) {
        return transactionGetService.getFilteredByMonthsTransactions(type.trim(), username.trim(), minMonth, maxMonth, year);
    }

    @GetMapping("/filtered-by-amount")
    public List<Transaction> getFilteredByAmountTransactions(@RequestParam String username,
                                                             @RequestParam String type,
                                                             @RequestParam(required = false) long minAmount,
                                                             @RequestParam long maxAmount) {
        return transactionGetService.getFilteredByAmountTransactions(type.trim(), username.trim(), minAmount, maxAmount);
    }

    @GetMapping("/filtered-_by-_date")
    public List<Transaction> getFilteredByDateTransactions(@RequestParam String username,
                                                           @RequestParam String type,
                                                           @RequestParam LocalDate date) {
        return transactionGetService.getTransactionsByDate(username.trim(), type.trim(), date);
    }

    @GetMapping("/filtered-_by-_date-_and-_category")
    public List<Transaction> getFilteredByDateAndCategoryTransactions(@RequestParam String username,
                                                                      @RequestParam String type,
                                                                      @RequestParam LocalDate date,
                                                                      @RequestParam long categoryId) {
        return transactionGetService.getTransactionsByCategoryAndDate(username.trim(), type.trim(), date, categoryId);
    }

    @GetMapping("/filtered-_by-_month-_and-_category")
    public List<Transaction> getFilteredTransactionsByMonthAndCategory(@RequestParam String username,
                                                                       @RequestParam String type,
                                                                       @RequestParam(required = false) short minMonth,
                                                                       @RequestParam(required = false) short maxMonth,
                                                                       @RequestParam int year,
                                                                       @RequestParam long categoryId) {
        return transactionGetService.getFilteredByMonthsTransactions(type.trim(),
                username.trim(),
                minMonth,
                maxMonth,
                year,
                categoryId);
    }

    @GetMapping("/sorted-by-amount")
    public List<Transaction> getSortedByAmountTransactions(@RequestParam String username,
                                                           @RequestParam String type,
                                                           @RequestParam(required = false) boolean isIncreasedSort) {
        return transactionGetService.getSortedByAmountTransactions(username.trim(), type.trim(), isIncreasedSort);
    }

    @GetMapping("/limited-number-of-transactions")
    public List<Transaction> getLimitedNumberOfTransactions(@RequestParam String username,
                                                            @RequestParam String type,
                                                            @RequestParam int lowLimit,
                                                            @RequestParam int highLimit) {
        return transactionGetService.getLimitedNumberOfTransactions(username.trim(), type.trim(), lowLimit, highLimit);
    }
}
