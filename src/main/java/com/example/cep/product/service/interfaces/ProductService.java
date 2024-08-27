package com.example.cep.product.service.interfaces;

import com.example.cep.common.PageDto;
import com.example.cep.product.dto.ProductResponseDto;
import org.springframework.data.domain.Page;

public interface ProductService {
  Page<ProductResponseDto> getAllProducts(PageDto pageDto);
  Page<ProductResponseDto> getCuProducts(PageDto pageDto);
  Page<ProductResponseDto> getGsProducts(PageDto pageDto);
  Page<ProductResponseDto> getEmartProducts(PageDto pageDto);
}
