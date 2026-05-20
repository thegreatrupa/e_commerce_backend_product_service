package com.example.product_service.service;

import com.example.product_service.entity.Product;
import com.example.product_service.exception.ForbiddenException;
import com.example.product_service.exception.ResourceNotFoundException;
import com.example.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product save(Product product, Long sellerId){
        product.setSellerId(sellerId);
        return productRepository.save(product);
    }

    public List<Product> findAll(){
        return productRepository.findByStockGreaterThan(0);
    }
    public Product findById(Long id, boolean forUser){
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if(forUser && product.getStock() == 0) throw new ResourceNotFoundException("Product not found");
        return product;
    }

    @PreAuthorize("hasRole('USER')")
    public Product updateProduct(Long id, Product updated, Long sellerId) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new ForbiddenException("You are not allowed to update this product");
        }

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());

        return productRepository.save(existing);
    }


    public void delete(Long id, Long sellerId){
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!existing.getSellerId().equals(sellerId)) {
            throw new ForbiddenException("You are not allowed to delete this product");
        }

        productRepository.deleteById(id);
    }

    public List<Product> getSellerProducts(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }
}
