package com.example.product_mock.model;

import lombok.*;
import jakarta.persistence.*; // JPA (Hibernate)
import jakarta.validation.constraints.*;



@Entity
@Table(name = "products")
@Data @Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank //строка не может быть пустой или null
    private String name;

    @NotBlank
    private String category;

    @PositiveOrZero // юольше или равно 0
    private double price;

    @PositiveOrZero
    private int stock;

    public Product() {
    }

    public Product(Long id, String name, String category, double price, int stock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }
}