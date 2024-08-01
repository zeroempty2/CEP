package com.example.cep.product.repository;

import com.example.cep.product.entity.Product;
import java.util.List;

public interface ProductRepositoryCustom {
  void saveAll(List<Product> products);
}
