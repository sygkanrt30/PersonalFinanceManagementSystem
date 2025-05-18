package ru.pratice.pet_project.personal_finance_management_system.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.pratice.pet_project.personal_finance_management_system.entities.Category;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Modifying
    @Query(value = "update categories set name = :name where id= :id", nativeQuery = true)
    void update(long id, String name);

    @Query(value = "select * from categories where id = :id", nativeQuery = true)
    Optional<Category> findByCategoryId(long id);
}
