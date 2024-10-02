package com.example.cep.product.entity;

import com.example.cep.util.TimeStamped;
import com.example.cep.util.enums.ConvenienceClassification;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
  private String productName;

  @Column
  private String productPrice;

  @Column
  private String productImg;

  @Column
  private String dumName;

  @Column
  private String dumImg;

  @Column
  private String eventClassification;

  @Column
  private ConvenienceClassification convenienceClassification;

  @Column
  private String productHash;


  @Builder
  public Product(String productName,String productPrice,String productImg, String dumName, String dumImg, String eventClassification,ConvenienceClassification convenienceClassification) {
    this.productName = productName;
    this.productPrice = productPrice;
    this.productImg = productImg;
    this.dumName = dumName;
    this.dumImg = dumImg;
    this.eventClassification = eventClassification;
    this.convenienceClassification = convenienceClassification;
    this.productHash = generateSHA256Hash(productName,eventClassification,convenienceClassification);
  }

  public String generateSHA256Hash(String productName, String eventClassification, ConvenienceClassification convenienceClassification) {
    try {
      String input = productName + eventClassification + convenienceClassification;
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

      // 바이트 배열을 16진수 문자열로 변환
      StringBuilder hexString = new StringBuilder();
      for (byte b : encodedhash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) hexString.append('0');
        hexString.append(hex);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

}
