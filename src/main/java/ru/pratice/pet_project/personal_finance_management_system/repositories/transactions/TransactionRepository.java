package ru.pratice.pet_project.personal_finance_management_system.repositories.transactions;

import lombok.NonNull;
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
    List<Transaction> findTransactionsByOwnerName(@NonNull String owner);

    @Query(value = "select * from transactions where type = :type", nativeQuery = true)
    List<Transaction> findTransactionByType(@NonNull String type);

    @Query(value = "select * from transactions where category_id = :category", nativeQuery = true)
    List<Transaction> findTransactionByCategoryId(@NonNull Category category);

    @Query(value = "select * from transactions where date = :localDate", nativeQuery = true)
    List<Transaction> findTransactionByDate(@NonNull LocalDate localDate);

    @Modifying
    @Query(value = "delete from transactions where owner = :owner", nativeQuery = true)
    void deleteTransactionByOwner(@NonNull String owner);

    @Modifying
    @Query(value = "delete from transactions where type = :type", nativeQuery = true)
    void deleteTransactionByType(@NonNull String type);

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
                @NonNull LocalDate date,
                String description,
                @NonNull String owner,
                @NonNull String type,
                @NonNull Long amount,
                @NonNull Long category);

    @Modifying
    @Query(value = "update transactions set amount = :amount where id = :id", nativeQuery = true)
    void updateAmount(Long id, Long amount);

    @Modifying
    @Query(value = "update transactions set date = :date where id = :id", nativeQuery = true)
    void updateDate(Long id, LocalDate date);

    @Modifying
    @Query(value = "update transactions set description = :description where id = :id", nativeQuery = true)
    void updateDescription(Long id, String description);

    @Modifying
    @Query(value = "update transactions set category_id = :categoryId where id = :id", nativeQuery = true)
    void updateCategoryId(Long id, Long categoryId);
}
