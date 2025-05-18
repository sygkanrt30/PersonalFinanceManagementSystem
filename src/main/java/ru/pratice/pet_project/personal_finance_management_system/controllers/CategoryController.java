package ru.pratice.pet_project.personal_finance_management_system.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pratice.pet_project.personal_finance_management_system.entities.Category;
import ru.pratice.pet_project.personal_finance_management_system.services.categories.CategoryService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/categories")
public class CategoryController {
    CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping(path = "/get/{id}")
    public Category getCategory(@PathVariable(name = "id") long id) {
        return categoryService.getCategoryById(id);
    }
    //служебный endpoint
    @DeleteMapping(path = "/delete/{id}")
    public void deleteCategory(@PathVariable(name = "id") long id) {
        categoryService.deleteCategoryById(id);
    }

    @PostMapping("/save-category")
    public void saveCategory(@RequestBody Category category) {
        categoryService.saveCategory(category);
    }

    @PatchMapping("/update-name")
    public void updateCategory(@RequestParam long id, @RequestParam String name) {
        categoryService.updateName(id, name.trim());
    }
}
