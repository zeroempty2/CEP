package com.example.cep.Favorite.dto;

import com.example.cep.util.enums.ConvenienceClassification;
import java.util.List;

public record FavoriteSearchRequestDto(String keyword, List<ConvenienceClassification> convenienceClassifications, List<String> eventClassifications,String inProgress) {

}
