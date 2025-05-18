package ru.pratice.pet_project.personal_finance_management_system.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pratice.pet_project.personal_finance_management_system.entities.Category;
import ru.pratice.pet_project.personal_finance_management_system.entities.Transaction;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "select * from transactions where username = :username", nativeQuery = true)
    List<Transaction> findTransactionsByUsername(String username);

    @Query(value = "select * from transactions " +
            "where category_id = :category and username = :name and type = :type", nativeQuery = true)
    List<Transaction> findTransactionByCategoryId(Category category,
                                                  String username,
                                                  String type);

    @Query(value = "select * from transactions where type = :type and username = :username", nativeQuery = true)
    List<Transaction> findTransactionsByType(String username, String type);

    @Query(value = "select * from transactions where type = :type and username = :username and date = :date", nativeQuery = true)
    List<Transaction> findTransactionByDate(String username, String type, LocalDate date);

    @Query(value = "select * from transactions " +
            "where type = :type and username = :username and date = :date and category_id = :category", nativeQuery = true)
    List<Transaction> findTransactionByCategoryIdAndDate(String username,
                                                         String type,
                                                         LocalDate date,
                                                         Category category);

    @Query(value = "select sum(amount) from transactions where username = :username and type = :type", nativeQuery = true)
    long sumOfTransactionsAmountWithTypeConsumption(String username, String type);

    @Query(value = "select * from transactions where type = :type and username = :username order by amount", nativeQuery = true)
    List<Transaction> findAndSortedByAscTransactionsByType(String username, String type);

    @Query(value = "select * from transactions where type = :type and username = :username order by amount desc", nativeQuery = true)
    List<Transaction> findAndSortedByDescTransactionsByType(String username, String type);

    @Query(value = "select * from transactions " +
            "where type = :type and " +
            "username = :username and " +
            "amount between :minAmount and :maxAmount ", nativeQuery = true)
    List<Transaction> filteredByAmountTransactions(String username, String type, long minAmount, long maxAmount);

    @Modifying
    @Query(value = "delete from transactions where username = :username", nativeQuery = true)
    void deleteTransactionByUsername(String username);

    @Modifying
    @Query(value = "delete from transactions where type = :type and username = :username", nativeQuery = true)
    void deleteTransactionByType(String type, String username);

    @Modifying
    @Query(value = "update transactions set " +
            "type = :type, " +
            "username = :username, " +
            "amount = :amount, " +
            "category_id = :category, " +
            "description = :description, " +
            "date = :date " +
            "where id = :id", nativeQuery = true)
    void update(Long id,
                LocalDate date,
                String description,
                String username,
                String type,
                long amount,
                long category);

    @Modifying
    @Query(value = "update transactions set amount = :amount where id = :id", nativeQuery = true)
    void updateAmount(long id, long amount);

    @Modifying
    @Query(value = "update transactions set date = :date where id = :id", nativeQuery = true)
    void updateDate(long id, LocalDate date);

    @Modifying
    @Query(value = "update transactions set description = :description where id = :id", nativeQuery = true)
    void updateDescription(long id, String description);

    @Modifying
    @Query(value = "update transactions set category_id = :categoryId where id = :id", nativeQuery = true)
    void updateCategoryId(long id, long categoryId);

    @Modifying
    @Query(value = "update transactions set username = :newUsername where username = :username", nativeQuery = true)
    void updateName(String newUsername, String username);
}
