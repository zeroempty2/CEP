package com.example.cep.product.service;


import com.example.cep.common.PageDto;
import com.example.cep.product.dto.ProductRequestDto;
import com.example.cep.product.dto.ProductResponseDto;
import com.example.cep.product.repository.ProductRepository;
import com.example.cep.product.service.interfaces.ProductService;
import com.example.cep.util.enums.ConvenienceClassification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponseDto> getProducts(PageDto pageDto,
      ProductRequestDto productRequestDto) {
    return productRepository.findProducts(pageDto,productRequestDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponseDto> getAllProducts(PageDto pageDto) {
    return  productRepository.findAllProducts(pageDto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponseDto> getCuProducts(PageDto pageDto) {
    return productRepository.pagingProducts(pageDto, ConvenienceClassification.CU);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponseDto> getGsProducts(PageDto pageDto) {
    return productRepository.pagingProducts(pageDto, ConvenienceClassification.GS25);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProductResponseDto> getEmartProducts(PageDto pageDto) {
    return productRepository.pagingProducts(pageDto, ConvenienceClassification.EMART24);
  }
}
