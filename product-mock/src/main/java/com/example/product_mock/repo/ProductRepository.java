package com.example.product_mock.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.product_mock.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
