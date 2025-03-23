package ru.pratice.pet_project.personal_finance_management_system.repositories.transactions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.Category;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(nullable = false)
    private Long amount;
    @NonNull
    @Column(nullable = false)
    private LocalDate date;
    @NonNull
    @Column(nullable = false)
    private String type;
    @NonNull
    @Column(nullable = false)
    private String username;
    private String description;
    @NonNull
    @OneToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}