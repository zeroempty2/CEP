package com.example.cep.user.repository;

public interface UserRepositoryQuery {
  boolean existsByValidationContents(String validationContents,String validationClassification);
}
