package ru.pratice.pet_project.personal_finance_management_system.repositories.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users where name = :name", nativeQuery = true)
    Optional<User> findUserByName(String name);

    @Query(value = "select * from users where email = :email", nativeQuery = true)
    Optional<User> findUserByEmail(String email);

    @Modifying
    @Query(value = "delete from users where id= :id", nativeQuery = true)
    void deleteUserById(Long id);

    @Modifying
    @Query(value = "delete from users where name= :name", nativeQuery = true)
    void deleteUserByName(String name);

    @Modifying
    @Query(value = "delete from users where email= :email", nativeQuery = true)
    void deleteUserByEmail(String email);

    @Modifying
    @Query(value = "update users set " +
            "email = :email, " +
            "name = :name, " +
            "password = :password, " +
            "birth = :birthday, " +
            "age = :age " +
            "where id = :id",
            nativeQuery = true)
    void updateUser(Long id, String email, String name, String password, LocalDate birthday, Integer age);

    @Modifying
    @Query(value = "update users set name = :name where id= :id", nativeQuery = true)
    void updateName(Long id, String name);

    @Modifying
    @Query(value = "update users set email = :email where id= :id", nativeQuery = true)
    void updateEmail(Long id, String email);

    @Modifying
    @Query(value = "update users set password = :password where id= :id", nativeQuery = true)
    void updatePassword(Long id, String password);

    @Modifying
    @Query(value = "update users set birth = :birth, age = :age where id= :id", nativeQuery = true)
    void updateBirth(Long id, LocalDate birth, int age);
}
