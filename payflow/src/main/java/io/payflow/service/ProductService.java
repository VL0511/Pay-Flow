package io.payflow.service;

import io.payflow.dto.ProductDTO;
import io.payflow.exception.ResourceNotFoundException;
import io.payflow.model.Product;
import io.payflow.model.User;
import io.payflow.repository.CategoryRespository;
import io.payflow.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRespository categoryRespository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return getProductOrThrow(id);
    }

    public Optional<Product> findByIdOptional(Long id) {
        return productRepository.findById(id);
    }

    public Product save(ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        return productRepository.save(product);
    }

    public Product update(Long id, ProductDTO productDTO) {
        Product existing = getProductOrThrow(id);
        updateProductFields(existing, productDTO);
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        Product existing = getProductOrThrow(id);
        productRepository.delete(existing);
    }

    public Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public void updateProductFields(Product existing, ProductDTO productDTO) {
        existing.setName(productDTO.getName());
        existing.setDescription(productDTO.getDescription());
        existing.setPrice(new BigDecimal(productDTO.getPrice()));
        existing.setCategory(categoryRespository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
    }

    public Product convertToEntity(ProductDTO productDTO) {
        return Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(new BigDecimal(productDTO.getPrice()))
                .quantity(Integer.parseInt(productDTO.getQuantity()))
                .category(categoryRespository.findById(productDTO.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found")))
                .build();
    }
}
