package com.example.cep.product.service.interfaces;

import com.example.cep.common.StatusResponseDto;

public interface ProductService {
  StatusResponseDto crawlCuProducts();
  StatusResponseDto crawlGsProducts();
}
