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
  private String ProductName;
  private String ProductPrice;
  private String ProductImg;
  private String EventClassification;
}
