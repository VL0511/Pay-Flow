package io.payflow.controller;

import io.payflow.dto.ProductDTO;
import io.payflow.exception.InternalServerErrorException;
import io.payflow.exception.MethodNotAllowedException;
import io.payflow.exception.ResourceNotFoundException;
import io.payflow.model.Product;
import io.payflow.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        List<Product> products = productService.findAll();
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found.");
        }

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id, HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod())) {
            throw new MethodNotAllowedException("GET method is not allowed here.");
        }

        Product product = productService.findById(id);
        if (product == null) {
            throw new ResourceNotFoundException("Product # " + id);
        }

        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductDTO productDTO) {
        try {

            Product product = productService.save(productDTO);
            return ResponseEntity.status(201).body(product);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error while creating product.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Product updatedProduct = productService.update(id, productDTO);

        if (updatedProduct == null) {
            throw new ResourceNotFoundException("Product #" + id);
        }

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
