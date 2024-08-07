package com.example.cep.Favorite.service.interfaces;

import com.example.cep.Favorite.dto.FavoriteRequestDto;
import com.example.cep.common.StatusResponseDto;


public interface FavoriteService {
  StatusResponseDto addFavorite(FavoriteRequestDto requestDto, Long userId);
}
