package ru.pratice.pet_project.personal_finance_management_system.repositories.transactions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.Category;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "select * from transactions where owner = :owner", nativeQuery = true)
    List<Transaction> findTransactionsByUsername(String owner);

    @Query(value = "select * from transactions " +
            "where category_id = :category and name = :name and type = :type", nativeQuery = true)
    List<Transaction> findTransactionByCategoryId(Category category,
                                                  String name,
                                                  String type);

    @Query(value = "select * from transactions where type = :type and owner = :username", nativeQuery = true)
    List<Transaction> findTransactionsByType(String username, String type);

    @Query(value = "select * from transactions where type = :type and owner = :name and date = :date", nativeQuery = true)
    List<Transaction> findTransactionByDate(String name, String type, LocalDate date);

    @Query(value = "select * from transactions " +
            "where type = :type and owner = :name and date = :date and category_id = :category", nativeQuery = true)
    List<Transaction> findTransactionByCategoryIdAndDate(String name,
                                                         String type,
                                                         LocalDate date,
                                                         Category category);

    @Modifying
    @Query(value = "delete from transactions where owner = :owner", nativeQuery = true)
    void deleteTransactionByUsername(String owner);

    @Modifying
    @Query(value = "delete from transactions where type = :type and owmer = :owner", nativeQuery = true)
    void deleteTransactionByType(String type, String owner);

    @Modifying
    @Query(value = "update transactions set " +
            "type = :type, " +
            "owner = :owner, " +
            "amount = :amount, " +
            "category_id = :category, " +
            "description = :description, " +
            "date = :date " +
            "where id = :id", nativeQuery = true)
    void update(Long id,
                LocalDate date,
                String description,
                String owner,
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
