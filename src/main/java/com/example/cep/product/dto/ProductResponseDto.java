package com.example.cep.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductResponseDto {
  private Long productId;
  private String productName;
  private String productPrice;
  private String productImg;
  private String eventClassification;
}
