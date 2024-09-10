package com.example.cep.product.repository;

import com.example.cep.product.entity.Product;
import com.example.cep.util.enums.ConvenienceClassification;
import java.util.List;
import org.springframework.data.repository.Repository;


public interface ProductRepository  extends Repository<Product, Long>, ProductRepositoryCustom ,ProductRepositoryQuery {
  void save(Product product);
  void saveAll(List<Product> products);
  List<Product> findByProductNameInAndEventClassificationInAndConvenienceClassificationIn(List<String> productNames, List<String> eventClassifications,
      List<ConvenienceClassification> convenienceClassifications);
}
