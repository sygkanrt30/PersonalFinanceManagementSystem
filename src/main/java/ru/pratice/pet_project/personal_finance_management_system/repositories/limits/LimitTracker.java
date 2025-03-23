package ru.pratice.pet_project.personal_finance_management_system.repositories.limits;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "limits")
public class LimitTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(nullable = false, unique = true)
    private String username;
    @NonNull
    @Column(name = "limit_amount", nullable = false)
    private Long limitAmount;
    @NonNull
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

}
