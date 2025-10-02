package com.example.product_mock.port;

import java.util.List;
import java.util.Optional;
import com.example.product_mock.model.Product;

public interface ProductPort {
    List<Product> findAll(); //поиск в бд или в памяти
    Optional<Product> findById(Long id); //возвращение продукта
    Product save(Product p); //сохранить продукт, если id нет-создать новый, если есть-обновить
    void deleteById(Long id); //удалить продукт по id
}
