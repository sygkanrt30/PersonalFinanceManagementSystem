package ru.pratice.pet_project.personal_finance_management_system.services.categories;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.Category;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.CategoryRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.EntityAlreadyExistsException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;

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
                () -> new ResourceNotFoundException("Category with id: " + id + " not found"));
    }

    public boolean isCategoryExistsByCategoryId(Long categoryId) {
        return categoryRepository.findByCategoryId(categoryId).isPresent();
    }

    public void deleteAllCategories() {
        categoryRepository.deleteAll();
    }

    public void deleteCategoryById(Long id) {
        isCategoryExistsById(id);
        log.info("Deleting category with id: {}", id);
        categoryRepository.deleteById(id);
    }

    public void saveCategory(Category category) {
        category.setName(category.getName().trim());
        try {
            categoryRepository.save(category);
        } catch (Exception e) {
            throw new EntityAlreadyExistsException("Category fields make duplicate");
        }
        log.info("Saving category {}", category);
    }

    @Transactional
    public void updateName(Long id, String name) {
        isCategoryExistsById(id);
        try {
            categoryRepository.update(id, name);
        } catch (Exception e) {
            throw new EntityAlreadyExistsException("Category fields make duplicate");
        }
        log.info("Updating category with id: {}", id);
    }

    private void isCategoryExistsById(Long id) {
        if (!categoryRepository.existsById(id))
            throw new ResourceNotFoundException("Category with id: " + id + " not found");
    }
}
