package com.example.cep.product.repository;

import com.example.cep.product.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  @Transactional
  public void saveAll(List<Product> products) {
    for (int i = 0; i < products.size(); i++) {
      entityManager.persist(products.get(i));
      if (i % 250 == 0) {
        entityManager.flush();
        entityManager.clear();
      }
    }
    entityManager.flush();
    entityManager.clear();
  }
}
