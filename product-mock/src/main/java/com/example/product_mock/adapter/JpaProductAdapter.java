package com.example.product_mock.adapter;

import com.example.product_mock.model.Product;
import com.example.product_mock.port.ProductPort;
import com.example.product_mock.repo.ProductRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnProperty(name = "app.db.lib", havingValue = "jpa", matchIfMissing = true)
public class JpaProductAdapter implements ProductPort {
    private final ProductRepository repo;
    public JpaProductAdapter(ProductRepository repo) {
        this.repo = repo;
    }

    @Override public List<Product> findAll(){
        return repo.findAll();
    }
    @Override public Optional<Product> findById(Long id){
        return repo.findById(id);
    }

    @Override public Product save(Product p){
        return repo.save(p);
    }

     @Override public void deleteById(Long id){
        repo.deleteById(id);
     }
}
