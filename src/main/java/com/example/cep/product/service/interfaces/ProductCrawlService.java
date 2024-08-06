package com.example.cep.product.service.interfaces;

import com.example.cep.common.StatusResponseDto;

public interface ProductCrawlService {
  StatusResponseDto crawlCuProducts();
  StatusResponseDto crawlGsProducts();
  StatusResponseDto crawlEmartProducts();
}
