package io.payflow.controller;

import com.paypal.exception.BaseException;
import io.payflow.dto.CategoryDTO;
import io.payflow.exception.BadRequestException;
import io.payflow.exception.InternalServerErrorException;
import io.payflow.exception.MethodNotAllowedException;
import io.payflow.exception.ResourceNotFoundException;
import io.payflow.model.Category;
import io.payflow.model.Product;
import io.payflow.repository.CategoryRespository;
import io.payflow.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        List<Category> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> findProductsByCategoryId(@PathVariable Long id) {
        List<Product> products = categoryService.findByProductsByCategoryId(id);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(@PathVariable Long id, HttpServletRequest request) {
        if (!HttpMethod.GET.equals(request.getMethod())) {
            throw new MethodNotAllowedException("GET method is not allowed here.");
        }

        Category category = categoryService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<Category> save(@RequestBody  CategoryDTO categoryDTO) {
        try {
            Category savedCategory = categoryService.save(categoryDTO);
            return ResponseEntity.status(201).body(savedCategory);
        } catch (Exception e) {
            throw new InternalServerErrorException("An unexpected error occurred while saving the category.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@RequestBody Long id, CategoryDTO categoryDTO) {
        Category updatedCategory = categoryService.update(id, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
