package com.example.cep.product.repository;

import com.example.cep.common.PageDto;
import com.example.cep.product.dto.ProductRequestDto;
import com.example.cep.product.dto.ProductResponseDto;
import com.example.cep.util.enums.ConvenienceClassification;
import org.springframework.data.domain.Page;

public interface ProductRepositoryQuery {
  Page<ProductResponseDto> findProducts(PageDto pageDto, ProductRequestDto productRequestDto);
  Page<ProductResponseDto> findAllProducts(PageDto pageDto);
  Page<ProductResponseDto> pagingProducts(PageDto pageDto, ConvenienceClassification convenienceClassification);
}
