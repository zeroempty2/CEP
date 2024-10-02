package com.example.cep.product.service;


import com.example.cep.common.PageDto;
import com.example.cep.product.dto.ProductRequestDto;
import com.example.cep.product.dto.ProductResponseDto;
import com.example.cep.product.entity.Product;
import com.example.cep.product.repository.ProductRepository;
import com.example.cep.product.service.interfaces.ProductService;
import com.example.cep.util.enums.ConvenienceClassification;
import java.util.List;
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

  @Override
  public List<Product> findByProductNameInAndEventClassificationInAndConvenienceClassificationIn(List<String> productNames, List<String> eventClassifications,
      List<ConvenienceClassification> convenienceClassifications) {
    return productRepository.findByProductNameInAndEventClassificationInAndConvenienceClassificationIn(productNames,eventClassifications,convenienceClassifications);
  }

  @Override
  public List<Product> findByProductHashIn(List<String> productHashes) {
    return productRepository.findByProductHashIn(productHashes);
  }

  @Override
  @Transactional
  public void deleteAllByConvenienceClassification(
      ConvenienceClassification convenienceClassification) {
    productRepository.deleteAllByConvenienceClassification(convenienceClassification);
  }

  @Override
  @Transactional
  public void deleteAllProduct() {
    deleteAllByConvenienceClassification(ConvenienceClassification.CU);
    deleteAllByConvenienceClassification(ConvenienceClassification.GS25);
    deleteAllByConvenienceClassification(ConvenienceClassification.EMART24);
  }
}
