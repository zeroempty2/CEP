package com.example.cep.product.controller;

import com.example.cep.common.StatusResponseDto;
import com.example.cep.product.service.interfaces.ProductCrawlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products/crawl")
public class ProductCrawlController {
  private final ProductCrawlService productCrawlService;

  @GetMapping("/cu")
  public ResponseEntity<StatusResponseDto> crawlCuProducts(){
    StatusResponseDto statusResponseDto = productCrawlService.crawlCuProducts();
    return ResponseEntity.ok().body(statusResponseDto);
  }

  @GetMapping("/gs")
  public ResponseEntity<StatusResponseDto> crawlGsProducts(){
    StatusResponseDto statusResponseDto = productCrawlService.crawlGsProducts();
    return ResponseEntity.ok().body(statusResponseDto);
  }

  @GetMapping("/emart")
  public ResponseEntity<StatusResponseDto> crawlEmartProducts(){
    StatusResponseDto statusResponseDto = productCrawlService.crawlEmartProducts();
    return ResponseEntity.ok().body(statusResponseDto);
  }
}
