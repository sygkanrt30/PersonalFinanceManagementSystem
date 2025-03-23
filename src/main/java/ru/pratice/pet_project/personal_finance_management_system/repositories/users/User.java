package ru.pratice.pet_project.personal_finance_management_system.repositories.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(nullable = false, unique = true)
    private String username;
    @NonNull
    @Column(nullable = false, unique = true)
    private String email;
    @NonNull
    @Column(nullable = false)
    private String password;
    @NonNull
    @Column(nullable = false)
    private LocalDate birth;
    private int age;
}