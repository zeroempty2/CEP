package com.example.cep.user.repository;


import com.example.cep.user.entity.User;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User,Long>,UserRepositoryQuery {
  void save(User user);
  Optional<User> findById(Long userId);
  Optional<User> findByUsername(String username);
  boolean existsByUsername(String username);
}
