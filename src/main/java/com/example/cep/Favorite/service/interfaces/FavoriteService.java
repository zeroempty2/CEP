package com.example.cep.Favorite.service.interfaces;

import com.example.cep.Favorite.dto.FavoriteCheckResponseDto;
import com.example.cep.Favorite.dto.FavoriteRequestDto;
import com.example.cep.Favorite.dto.FavoriteResponseDto;
import com.example.cep.Favorite.dto.FavoriteSearchRequestDto;
import com.example.cep.Favorite.entity.Favorite;
import com.example.cep.common.PageDto;
import com.example.cep.common.StatusResponseDto;
import com.example.cep.security.UserDetailsImpl;
import org.springframework.data.domain.Page;



public interface FavoriteService {
  StatusResponseDto addFavorite(FavoriteRequestDto requestDto, Long userId);
  Page<FavoriteResponseDto> getFavorites(Long userId, PageDto pageDto);
  Boolean deleteFavorite(Long userId,Long favoriteId);
  Favorite findFavoriteById(Long favoriteId);
  Favorite findFavoriteByProductName(String productName);
  StatusResponseDto requestFavorite(FavoriteRequestDto requestDto, Long userId);
  Page<FavoriteCheckResponseDto> getCheckingFavorite(Long userId, PageDto pageDto,
      FavoriteSearchRequestDto favoriteSearchRequestDto);
}
