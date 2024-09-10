package com.example.cep.Favorite.dto;

import com.example.cep.util.enums.ConvenienceClassification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FavoriteCheckResponseDto {
  private Long favoriteId;
  private String productName;
  private String productImg;
  private String productPrice;
  private ConvenienceClassification convenienceClassification;
  private String eventClassification;
  private Boolean isSale;
}
