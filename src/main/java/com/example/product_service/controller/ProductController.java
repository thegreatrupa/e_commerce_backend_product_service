package com.example.product_service.controller;

import com.example.product_service.entity.Product;
import com.example.product_service.service.ProductService;
import com.example.product_service.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final JwtUtil jwtUtil;

    public ProductController(ProductService productService, JwtUtil jwtUtil) {
        this.productService = productService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public Product create(@RequestBody Product product, @RequestHeader("Authorization") String authHeader){
        String authToken = authHeader.substring(7);
        Long sellerId = jwtUtil.getUserId(authToken);
        return productService.save(product, sellerId);
    }

    @GetMapping
    public List<Product> getAll(){
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product get(@PathVariable Long id){
        return productService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);
        Long sellerId = jwtUtil.getUserId(token);

        productService.delete(id, sellerId);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product, @RequestHeader("Authorization") String authHeader){
        String token = authHeader.substring(7);
        Long sellerId = jwtUtil.getUserId(token);

        return productService.updateProduct(id, product, sellerId);
    }

    @GetMapping("/seller")
    public List<Product> getSellerProducts(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long sellerId = jwtUtil.getUserId(token);

        return productService.getSellerProducts(sellerId);
    }
}
