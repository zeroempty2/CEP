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
public class FavoriteResponseDto {
  private Long favoriteId;
  private String productName;
  private String productImg;
  private ConvenienceClassification convenienceClassification;
  private String eventClassification;
}
