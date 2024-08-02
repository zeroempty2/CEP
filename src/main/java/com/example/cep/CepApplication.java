package com.example.cep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CepApplication {

  public static void main(String[] args) {
    SpringApplication.run(CepApplication.class, args);
  }

}
