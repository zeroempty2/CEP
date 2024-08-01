package com.example.cep.product.controller;

import com.example.cep.common.StatusResponseDto;
import com.example.cep.product.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
  private final ProductService productService;

  @GetMapping("/cu")
  public ResponseEntity<StatusResponseDto> getCuProducts(){
    StatusResponseDto statusResponseDto = productService.crawlCuProducts();
    return ResponseEntity.ok().body(statusResponseDto);
  }


}
