package com.example.cep.Favorite.dto;

import com.example.cep.util.enums.ConvenienceClassification;

public record FavoriteRequestDto(String productName, String productImg, ConvenienceClassification convenienceClassification, Long userId) {

}
