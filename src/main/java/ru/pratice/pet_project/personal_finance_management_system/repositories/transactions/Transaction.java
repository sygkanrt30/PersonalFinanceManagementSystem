package ru.pratice.pet_project.personal_finance_management_system.repositories.transactions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
    private Long amount;
    @NonNull
    private LocalDate date;
    @NonNull
    private String type;
    @NonNull
    private String owner;
    private String description;
    @NonNull
    @OneToOne
    @JoinColumn(name = "category_id")
    private Category category;
}