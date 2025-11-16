package com.example.product_mock.service;

import com.example.product_mock.model.Product;
import com.example.product_mock.port.ProductPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProductService {
    private final ProductPort productPort;
    public ProductService(ProductPort productPort) { this.productPort = productPort; }

    private void delay() {
        try { Thread.sleep(ThreadLocalRandom.current().nextLong(500, 1501)); }
        catch (InterruptedException ignored) {}
    }

    public List<Product> findAll() {
        delay();
        return productPort.findAll();
    }

    public Product findById(Long id) {
        delay();
        return productPort.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found"));
    }

    public Product create(Product p) {
        delay();
        return productPort.save(p);
    }

    public Product update(Long id, Product p) {
        delay();
        Product old = productPort.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found"));
        old.setName(p.getName());
        old.setCategory(p.getCategory());
        old.setPrice(p.getPrice());
        old.setStock(p.getStock());
        return productPort.save(old);
    }

    public void delete(Long id) {
        delay();
        // можно мягко: если не существует — 404
        if (productPort.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found");
        }
        productPort.deleteById(id);
    }
}
