package com.example.product_service.controller;

import com.example.product_service.entity.Product;
import com.example.product_service.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Product create(@RequestBody Product product,
                          @RequestHeader("X-User-Id") String userId) {
        Long sellerId = Long.valueOf(userId);
        return productService.save(product, sellerId);
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return productService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @RequestHeader("X-User-Id") String userId) {
        Long sellerId = Long.valueOf(userId);
        productService.delete(id, sellerId);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id,
                          @RequestBody Product product,
                          @RequestHeader("X-User-Id") String userId) {
        Long sellerId = Long.valueOf(userId);
        return productService.updateProduct(id, product, sellerId);
    }

    @GetMapping("/seller")
    public List<Product> getSellerProducts(@RequestHeader("X-User-Id") String userId) {
        Long sellerId = Long.valueOf(userId);
        return productService.getSellerProducts(sellerId);
    }

    @PostMapping("/{id}/decrement")
    public ResponseEntity<Void> decrementStock(@PathVariable Long id,
                                               @RequestParam int quantity,
                                               @RequestHeader("X-User-Id") String userId) {
        Product p = productService.findById(id);
        if (p.getStock() < quantity) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Long currentUserId = Long.valueOf(userId);

        int newStock = p.getStock() - quantity;
        if (newStock == 0) {
            productService.delete(id, p.getSellerId());
        } else {
            p.setStock(newStock);
            productService.save(p, p.getSellerId());
        }
        return ResponseEntity.ok().build();
    }
}