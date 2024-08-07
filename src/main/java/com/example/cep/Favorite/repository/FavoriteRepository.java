package com.example.cep.Favorite.repository;

import com.example.cep.Favorite.entity.Favorite;
import org.springframework.data.repository.Repository;

public interface FavoriteRepository extends Repository<Favorite, Long> {
  void save(Favorite favorite);
}
