package com.example.cep.user.entity;

import com.example.cep.util.TimeStamped;
import com.example.cep.util.enums.UserRoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name="users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User extends TimeStamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 25, nullable = false, unique = true)
  private String username;

  @Column
  @Enumerated(EnumType.STRING)
  private UserRoleEnum role;

  @Column(nullable = false)
  private String password;

  @Column
  private String email;

  @Column
  private String nickName;

  @Builder
  public User(String username, String password, String nickName, String email, UserRoleEnum role){
    this.username = username;
    this.password = password;
    this.nickName = nickName;
    this.email =  email;
    this.role = role;
  }


}
