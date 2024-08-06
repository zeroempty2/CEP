package com.example.cep.product.repository;

import com.example.cep.product.entity.Product;
import java.util.List;
import org.springframework.data.repository.Repository;


public interface ProductRepository  extends Repository<Product, Long>, ProductRepositoryCustom ,ProductRepositoryQuery {
  void save(Product product);
  void saveAll(List<Product> products);
}
