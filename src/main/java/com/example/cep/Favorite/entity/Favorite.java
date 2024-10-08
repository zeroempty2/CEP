package com.example.cep.Favorite.entity;

import com.example.cep.util.TimeStamped;
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
public class Favorite extends TimeStamped {
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
  private String eventClassification;

  @Column
  private String dumName;

  @Column
  private String dumImg;

  @Column
  private String productHash;

  @Column
  private Long userId;

  @Builder
  public Favorite(String productName,String productImg,ConvenienceClassification convenienceClassification,String eventClassification,String dumName, String dumImg, String productHash, Long userId) {
    this.productName = productName;
    this.productImg = productImg;
    this.convenienceClassification = convenienceClassification;
    this.eventClassification = eventClassification;
    this.dumName = dumName;
    this.dumImg = dumImg;
    this.productHash = productHash;
    this.userId = userId;
  }

  //메서드
  public boolean isOwner(Long userId){
    return this.userId.equals(userId);
  }


}
