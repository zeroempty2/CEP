package com.example.cep.product.entity;

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
public class Product extends TimeStamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String ProductName;

  @Column
  private String ProductPrice;

  @Column
  private String ProductImg;

  @Column
  private String EventClassification;

  @Column
  private ConvenienceClassification convenienceClassification;

  @Builder
  public Product(String productName,String productPrice,String productImg,String eventClassification,ConvenienceClassification convenienceClassification) {
    this.ProductName = productName;
    this.ProductPrice = productPrice;
    this.ProductImg = productImg;
    this.EventClassification = eventClassification;
    this.convenienceClassification = convenienceClassification;
  }


}
