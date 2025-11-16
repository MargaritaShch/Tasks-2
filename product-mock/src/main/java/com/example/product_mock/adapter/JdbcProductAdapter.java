package com.example.product_mock.adapter;

import com.example.product_mock.model.Product;
import com.example.product_mock.port.ProductPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnProperty(name = "app.db.lib", havingValue = "jdbc")
public class JdbcProductAdapter implements ProductPort {

    private final JdbcTemplate jdbc;

    public JdbcProductAdapter(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Product> findAll() {
        return jdbc.query(
                "SELECT id, name, category, price, stock FROM products ORDER BY id",
                (rs, i) -> new Product(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                )
        );
    }

    @Override
    public Optional<Product> findById(Long id) {
        var list = jdbc.query(
                "SELECT id, name, category, price, stock FROM products WHERE id = ?",
                (rs, i) -> new Product(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                ),
                id
        );
        return list.stream().findFirst();
    }

    @Override
    public Product save(Product p) {
        if (p.getId() == null) {
            // ВАРИАНТ А: PostgreSQL RETURNING
            Long id = jdbc.queryForObject(
                    "INSERT INTO products(name, category, price, stock) VALUES (?,?,?,?) RETURNING id",
                    Long.class,
                    p.getName(), p.getCategory(), p.getPrice(), p.getStock()
            );
            p.setId(id);
            return p;

            // ВАРИАНТ Б (если нужен кросс-СУБД): SimpleJdbcInsert
            // SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbc)
            //     .withTableName("products")
            //     .usingGeneratedKeyColumns("id");
            // Number key = insert.executeAndReturnKey(Map.of(
            //     "name", p.getName(),
            //     "category", p.getCategory(),
            //     "price", p.getPrice(),
            //     "stock", p.getStock()
            // ));
            // p.setId(key.longValue());
            // return p;
        } else {
            jdbc.update(
                    "UPDATE products SET name=?, category=?, price=?, stock=? WHERE id=?",
                    p.getName(), p.getCategory(), p.getPrice(), p.getStock(), p.getId()
            );
            return p;
        }
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM products WHERE id = ?", id);
    }
}
