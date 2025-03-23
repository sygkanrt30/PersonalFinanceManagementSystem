package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.repositories.categories.Category;
import ru.pratice.pet_project.personal_finance_management_system.services.categories.CategoryService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/categories")
public class CategoryController {
    private CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping(path = "{id}")
    public Category getCategory(@PathVariable(name = "id") Long id) {
        return categoryService.getCategoryById(id);
    }

    @DeleteMapping
    public void deleteAllCategories() {
        categoryService.deleteAllCategories();
    }

    @DeleteMapping(path = "{id}")
    public void deleteCategory(@PathVariable(name = "id") Long id) {
        categoryService.deleteCategoryById(id);
    }

    @PostMapping
    public void saveCategory(@RequestBody Category category) {
        categoryService.saveCategory(category);
    }

    @PatchMapping("/update_name")
    public void updateCategory(@RequestParam Long id, @RequestParam String name) {
        categoryService.updateName(id, name.trim());
    }
}
