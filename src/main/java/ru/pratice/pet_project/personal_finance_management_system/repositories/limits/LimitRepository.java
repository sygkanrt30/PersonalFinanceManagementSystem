package ru.pratice.pet_project.personal_finance_management_system.repositories.limits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<LimitTracker, Long> {

    @Query(value = "select * from limits where username = :username", nativeQuery = true)
    Optional<LimitTracker> findLimitByUsername(String username);

    @Query(value = "delete from limits where username = :username", nativeQuery = true)
    @Modifying
    void deleteLimitsByUsername(String username);

    @Query(value = "update limits set limit_amount = :limitAmount where id = :id", nativeQuery = true)
    @Modifying
    void updateLimitAmount(long id, long limitAmount);

    @Query(value = "update limits set total_amount = total_amount + :totalAmount where username = :username",
            nativeQuery = true)
    @Modifying
    void incrementTotalAmount(String username, long totalAmount);

    @Query(value = "update limits set total_amount =  total_amount - :totalAmount where username = :username",
            nativeQuery = true)
    @Modifying
    void decrementTotalAmount(String username, long totalAmount);

    @Query(value = "update limits set username = :username where username = :oldUsername", nativeQuery = true)
    @Modifying
    void updateUsername(String username, String oldUsername);
}
