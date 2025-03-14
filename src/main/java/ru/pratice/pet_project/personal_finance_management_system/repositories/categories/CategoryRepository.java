package ru.pratice.pet_project.personal_finance_management_system.repositories.categories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select * from categories where name = :name", nativeQuery = true)
    Optional<Category> findCategoryByName(String name);

    @Query(value = "select * from categories where category_id = :categoryId", nativeQuery = true)
    Optional<Category> findCategoryByCategory_id(Long categoryId);

    @Modifying
    @Query(value = "update categories set name = :name where id= :id", nativeQuery = true)
    void update(Long id, String name);

    @Query(value = "select * from categories where category_id = :categoryId", nativeQuery = true)
    Optional<Category> findByCategoryId(Long categoryId);
}
