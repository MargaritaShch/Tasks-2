package com.example.product_mock.config;

import com.example.product_mock.model.Product;
import com.example.product_mock.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SeedConfig {

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Value("${app.seed.reset:false}")
    private boolean seedReset;

    @Value("${app.seed.target:200}")
    private long targetCount;

    @Value("${app.seed.batch-size:200}")
    private int batchSize;

    // добавим переключатель реализации (jpa|jdbc). по умолчанию jpa
    @Value("${app.seed.impl:jpa}")
    private String seedImpl;

    @Bean
    CommandLineRunner seedProducts(ProductRepository jpaRepo, JdbcTemplate jdbc) {
        return args -> {
            if (!seedEnabled) return;

            if ("jdbc".equalsIgnoreCase(seedImpl)) {
                // --- JDBC сид ---
                if (seedReset) {
                    jdbc.update("DELETE FROM products");
                    // попытка сбросить последовательность (PG / MySQL)
                    try { jdbc.execute("ALTER SEQUENCE products_id_seq RESTART WITH 1"); } catch (Exception ignore) {}
                    try { jdbc.execute("ALTER TABLE products AUTO_INCREMENT = 1"); } catch (Exception ignore) {}
                }
                Long current = jdbc.queryForObject("SELECT COUNT(*) FROM products", Long.class);
                long toInsert = targetCount - (current == null ? 0 : current);
                if (toInsert <= 0) return;

                long startIndex = (current == null ? 0 : current) + 1;
                while (toInsert > 0) {
                    int portion = (int) Math.min(batchSize, toInsert);
                    List<Object[]> batch = new ArrayList<>(portion);
                    for (int i = 0; i < portion; i++) {
                        long n = startIndex + i;
                        var p = buildDeterministicProduct(n);
                        batch.add(new Object[]{p.getName(), p.getCategory(), p.getPrice(), p.getStock()});
                    }
                    jdbc.batchUpdate(
                            "INSERT INTO products(name, category, price, stock) VALUES (?, ?, ?, ?)",
                            batch
                    );
                    toInsert -= portion;
                    startIndex += portion;
                }

            } else {
                // --- JPA сид (как был) ---
                if (seedReset) {
                    jpaRepo.deleteAll();
                }
                long current = jpaRepo.count();
                long toInsert = targetCount - current;
                if (toInsert <= 0) return;

                List<Product> buffer = new ArrayList<>(batchSize);
                long startIndex = current + 1;

                while (toInsert > 0) {
                    buffer.clear();
                    int portion = (int) Math.min(batchSize, toInsert);
                    for (int i = 0; i < portion; i++) {
                        long n = startIndex + i;
                        buffer.add(buildDeterministicProduct(n));
                    }
                    jpaRepo.saveAll(buffer);
                    toInsert -= portion;
                    startIndex += portion;
                }
            }
        };
    }

    private Product buildDeterministicProduct(long n) {
        String padded = String.format("%06d", n);
        double price = BigDecimal.valueOf((n % 1000) + 0.99)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        int stock = (int) (n % 500);

        return Product.builder()
                .name("Item-" + padded)
                .category("LoadTest")
                .price(price)
                .stock(stock)
                .build();
    }
}
