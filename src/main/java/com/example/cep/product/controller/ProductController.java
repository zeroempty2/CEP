package com.example.cep.product.controller;

import com.example.cep.common.PageDto;
import com.example.cep.product.dto.ProductResponseDto;
import com.example.cep.product.service.interfaces.ProductService;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
  private final ProductService productService;

  @GetMapping
  public ResponseEntity<Page<ProductResponseDto>> getAllProducts(PageDto pageDto){
    Page<ProductResponseDto> productList = productService.getAllProducts(pageDto);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
    return ResponseEntity.ok().headers(headers).body(productList);
  }

 @GetMapping("/cu")
 public ResponseEntity<Page<ProductResponseDto>> getCuProducts(PageDto pageDto){
   Page<ProductResponseDto> productList = productService.getCuProducts(pageDto);
   HttpHeaders headers = new HttpHeaders();
   headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
   return ResponseEntity.ok().headers(headers).body(productList);
 }

  @GetMapping("/gs")
  public ResponseEntity<Page<ProductResponseDto>> getGsProducts(PageDto pageDto){
    Page<ProductResponseDto> productList = productService.getGsProducts(pageDto);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
    return ResponseEntity.ok().headers(headers).body(productList);
  }

  @GetMapping("/emart")
  public ResponseEntity<Page<ProductResponseDto>> getEmartProducts(PageDto pageDto){
    Page<ProductResponseDto> productList = productService.getEmartProducts(pageDto);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
    return ResponseEntity.ok().headers(headers).body(productList);
  }

}
