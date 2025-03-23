package ru.pratice.pet_project.personal_finance_management_system.repositories.categories;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(nullable = false, unique = true)
    private String name;
    @NonNull
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @JsonCreator
    public Category(@JsonProperty("id") @NonNull Long id,
                    @JsonProperty("categoryId") @NonNull Long categoryId,
                    @JsonProperty("name") @NonNull String name) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
    }
}
