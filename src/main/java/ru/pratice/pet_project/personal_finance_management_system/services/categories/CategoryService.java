package ru.pratice.pet_project.personal_finance_management_system.services.categories;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.Category;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.CategoryRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category with id: " + id + " not found", LocalTime.now()));
    }

    public boolean getCategoryByCategoryId(Category category) {
        return categoryRepository.findByCategoryId(category.getCategoryId()).isPresent();
    }

    public void deleteAllCategories() {
        categoryRepository.deleteAll();
    }

    public void deleteCategoryById(Long id) {
        if (categoryRepository.existsById(id)) {
            log.info("Deleting category with id: {}", id);
            categoryRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Category with id: " + id + " not found", LocalTime.now());
        }
    }

    public Category saveCategory(Category category) {
        try {
            category.setName(category.getName().trim());
            checkCategoryFields(category);
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
            throw new InvalidEntityException("Category fields can't be empty", LocalTime.now());
        }
        log.info("Saving category {}", category);
        return categoryRepository.save(category);
    }

    @Transactional
    public void updateName(Long id, String name) {
        if (categoryRepository.existsById(id)) {
            try {
                isNameMakeDuplicate(name.trim());
            } catch (NullPointerException e) {
                log.error(e.getMessage(), e);
                throw new InvalidEntityException("Category's name can't be null", LocalTime.now());
            }
            categoryRepository.update(id, name.trim());
            log.info("Updating category with id: {}", id);
        } else {
            throw new ResourceNotFoundException("Category with id: " + id + " not found", LocalTime.now());
        }
    }

    private boolean isNameMakeDuplicate(String name) {
        return categoryRepository.findCategoryByName(name).isPresent();
    }

    private boolean isCategoryIdMakeDuplicate(Long categoryId) {
        return categoryRepository.findCategoryByCategory_id(categoryId).isPresent();
    }

    private void checkCategoryFields(Category category) {
        if (isNameMakeDuplicate(category.getName())) {
            throw new InvalidEntityException("Category with name: " + category.getName() + " already exists",
                    LocalTime.now());
        } else if (isCategoryIdMakeDuplicate(category.getCategoryId())) {
            throw new InvalidEntityException("Category with category_id: " + category.getCategoryId() + " already exists",
                    LocalTime.now());
        }
    }
}
