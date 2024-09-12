package com.example.cep.product.dto;

import com.example.cep.util.enums.ConvenienceClassification;
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
  private String dumName;
  private String dumPrice;
  private String eventClassification;
  private ConvenienceClassification convenienceClassification;
}
