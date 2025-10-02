package com.example.product_mock.service;

import com.example.product_mock.model.Product;
import com.example.product_mock.port.ProductPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ProductService {
    private final ProductPort productPort;
    public ProductService(ProductPort productPort) {
        this.productPort = productPort;
    }

    //задержка
    private void delay() {
        try{ Thread.sleep(ThreadLocalRandom.current().nextLong(500, 1501));
        } catch (InterruptedException ignored) {}
    }

    //поиск всех продуктов
    public List<Product> findAll() {
        delay();
        return productPort.findAll();
    }

    //поиск по id, если продукта нет - выброс исключения
    public Product findById(Long id) {
        delay();
        return productPort.findById(id).orElseThrow();
    }

    //создать и сохранить новый продукт
    public Product create(Product p) {
        delay();
        return productPort.save(p);
    }

    //поиск старого продукта, обновление и сохаранение
    public Product update(Long id,Product p) {
        delay();
        Product old = productPort.findById(id).orElseThrow();
        old.setName(p.getName());
        old.setCategory(p.getCategory());
        old.setPrice(p.getPrice());
        old.setStock(p.getStock());
        return productPort.save(old);
    }

    //удаление продукта
    public void delete(Long id) {
        delay();
        productPort.deleteById(id);
    }
}
