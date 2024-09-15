package com.example.cep.product.service.interfaces;

import com.example.cep.common.PageDto;
import com.example.cep.product.dto.ProductRequestDto;
import com.example.cep.product.dto.ProductResponseDto;
import com.example.cep.product.entity.Product;
import com.example.cep.util.enums.ConvenienceClassification;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ProductService {
  Page<ProductResponseDto> getProducts(PageDto pageDto, ProductRequestDto productRequestDto);
  Page<ProductResponseDto> getAllProducts(PageDto pageDto);
  Page<ProductResponseDto> getCuProducts(PageDto pageDto);
  Page<ProductResponseDto> getGsProducts(PageDto pageDto);
  Page<ProductResponseDto> getEmartProducts(PageDto pageDto);
  List<Product> findByProductNameInAndEventClassificationInAndConvenienceClassificationIn(List<String> productNames, List<String> eventClassifications,
      List<ConvenienceClassification> convenienceClassifications);
  void deleteAllByConvenienceClassification(ConvenienceClassification convenienceClassification);
  void deleteAllProduct();
}
