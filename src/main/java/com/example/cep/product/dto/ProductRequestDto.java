package com.example.cep.product.dto;

import com.example.cep.util.enums.ConvenienceClassification;
import java.util.List;

public record ProductRequestDto(String keyword, List<ConvenienceClassification> convenienceClassifications, List<String> eventClassifications) {

}
