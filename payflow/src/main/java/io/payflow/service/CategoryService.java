package io.payflow.service;

import io.payflow.dto.CategoryDTO;
import io.payflow.exception.ResourceNotFoundException;
import io.payflow.model.Category;
import io.payflow.model.Product;
import io.payflow.repository.CategoryRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRespository categoryRepository;

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Long id){
        return categoryRepository.findById(id);
    }

    public Category save(CategoryDTO categoryDTO) {
        Category category = converToEntity(categoryDTO);
        return categoryRepository.save(category);
    }

    public Category update(Long id, CategoryDTO categoryDTO) {
        Category existing = getCategoryOrThrow(id);
        updateCategoryFields(existing, categoryDTO);
        return categoryRepository.save(existing);
    }

    public Optional<Category> findByIdOptional(Long id) {
        return categoryRepository.findById(id);
    }

    public void deleteCategory(Long id) {
        Category existing = getCategoryOrThrow(id);
        categoryRepository.delete(existing);
    }

    public Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public List<Product> findByProductsByCategoryId(Long id) {
        Category category = getCategoryOrThrow(id);
        return category.getProducts();
    }

    public void updateCategoryFields(Category existing, CategoryDTO categoryDTO) {
        existing.setName(categoryDTO.getName());
    }

    public Category converToEntity(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return category;
    }
}
