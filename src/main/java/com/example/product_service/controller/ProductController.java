package com.example.product_service.controller;

import com.example.product_service.dto.ProductResponse;
import com.example.product_service.entity.Product;
import com.example.product_service.exception.ResourceNotFoundException;
import com.example.product_service.service.ProductService;
import com.example.product_service.util.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody Product product,
                                  @RequestHeader("X-User-Id") String userId) {
        Long sellerId = Long.valueOf(userId);
        Product savedProduct = productService.save(product, sellerId);
        return ResponseEntity.ok(productMapper.toResponse(savedProduct));
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productService.findById(id, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                       @RequestHeader("X-User-Id") String userId) {
        Long sellerId = Long.valueOf(userId);
        productService.delete(id, sellerId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                          @RequestBody Product product,
                          @RequestHeader("X-User-Id") String userId) {
        Long sellerId = Long.valueOf(userId);
        Product updatedProduct = productService.updateProduct(id, product, sellerId);
        return ResponseEntity.ok(productMapper.toResponse(updatedProduct));
    }

    @GetMapping("/seller")
    public ResponseEntity<List<ProductResponse>> getSellerProducts(@RequestHeader("X-User-Id") String userId) {
        Long sellerId = Long.valueOf(userId);
        List<Product> productList = productService.getSellerProducts(sellerId);
        return ResponseEntity.ok(
                productList.stream()
                        .map(productMapper::toResponse).toList()
        );
    }

    @PostMapping("/{id}/decrement")
    public ResponseEntity<Void> decrementStock(@PathVariable Long id,
                                               @RequestParam int quantity,
                                               @RequestHeader("X-User-Id") String userId) {
        Product p = productService.findById(id, true);
        if (p.getStock() < quantity) {
            throw new ResourceNotFoundException("Available stock is not sufficient. Requested: " + quantity +
                    ", Available: " + p.getStock());
        }

        Long currentUserId = Long.valueOf(userId);

        int newStock = p.getStock() - quantity;

        p.setStock(newStock);
        productService.save(p, p.getSellerId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/increment")
    public ResponseEntity<Void> incrementStock(@PathVariable Long id,
                                               @RequestParam int quantity,
                                               @RequestHeader("X-User-Id") String userId) {
        Product p = productService.findById(id, false);

        int newStock = p.getStock() + quantity;
        p.setStock(newStock);
        productService.save(p, p.getSellerId());
        return ResponseEntity.ok().build();
    }
}