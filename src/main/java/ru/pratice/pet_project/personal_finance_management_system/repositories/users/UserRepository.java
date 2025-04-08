package ru.pratice.pet_project.personal_finance_management_system.repositories.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users where username = :name", nativeQuery = true)
    Optional<User> findUserByName(String name);

    @Query(value = "select * from users where email = :email", nativeQuery = true)
    Optional<User> findUserByEmail(String email);

    @Modifying
    @Query(value = "delete from users where id= :id", nativeQuery = true)
    void deleteUserById(long id);

    @Modifying
    @Query(value = "delete from users where username= :name", nativeQuery = true)
    void deleteUserByName(String name);

    @Modifying
    @Query(value = "update users set " +
            "email = :email, " +
            "username = :name, " +
            "password = :password, " +
            "birth = :birthday " +
            "where id = :id",
            nativeQuery = true)
    void updateUser(long id, String email, String name, String password, LocalDate birthday);

    @Modifying
    @Query(value = "update users set username = :name where id= :id", nativeQuery = true)
    void updateName(long id, String name);

    @Modifying
    @Query(value = "update users set email = :email where id= :id", nativeQuery = true)
    void updateEmail(long id, String email);

    @Modifying
    @Query(value = "update users set password = :password where id= :id", nativeQuery = true)
    void updatePassword(long id, String password);
}
