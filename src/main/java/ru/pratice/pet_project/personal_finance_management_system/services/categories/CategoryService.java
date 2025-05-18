package ru.pratice.pet_project.personal_finance_management_system.services.categories;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pratice.pet_project.personal_finance_management_system.entities.Category;
import ru.pratice.pet_project.personal_finance_management_system.repositories.CategoryRepository;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.InvalidEntityException;
import ru.pratice.pet_project.personal_finance_management_system.services.exceptions.ResourceNotFoundException;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {
    CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Category with id: " + id + " not found"));
    }

    public void deleteCategoryById(long id) {
        getCategoryById(id);
        categoryRepository.deleteById(id);
        log.info("Deleting category with id: {}", id);
    }

    public void saveCategory(Category category) {
        save(category);
        log.info("Saving category {}", category);
    }

    private void save(Category category) {
        try {
            category.setName(category.getName().trim());
            categoryRepository.save(category);
        } catch (Exception e) {
            throw new InvalidEntityException(e.getMessage());
        }
    }

    @Transactional
    public void updateName(long id, String name) {
        getCategoryById(id);
        update(id, name);
        log.info("Updating category with id: {}", id);
    }

    private void update(long id, String name) {
        try {
            categoryRepository.update(id, name);
        } catch (Exception e) {
            throw new InvalidEntityException(e.getMessage());
        }
    }
}
