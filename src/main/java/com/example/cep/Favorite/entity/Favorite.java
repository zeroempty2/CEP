package com.example.cep.Favorite.entity;

import com.example.cep.util.enums.ConvenienceClassification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Favorite {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String productName;

  @Column
  private String productImg;

  @Column
  private ConvenienceClassification convenienceClassification;

  @Column
  private Long userId;

  @Builder
  public Favorite(String productName,String productImg,ConvenienceClassification convenienceClassification,Long userId) {
    this.productName = productName;
    this.productImg = productImg;
    this.convenienceClassification = convenienceClassification;
    this.userId = userId;
  }



}
