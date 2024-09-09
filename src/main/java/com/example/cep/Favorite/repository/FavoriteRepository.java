package com.example.cep.Favorite.repository;

import com.example.cep.Favorite.entity.Favorite;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface FavoriteRepository extends Repository<Favorite, Long>,FavoriteRepositoryQuery {
  void save(Favorite favorite);
  void deleteById(Long id);
  Optional<Favorite> findById(Long id);
  Optional<Favorite> findByProductName(String productName);
}
