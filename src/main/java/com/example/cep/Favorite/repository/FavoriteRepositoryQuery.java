package com.example.cep.Favorite.repository;

import com.example.cep.Favorite.dto.FavoriteCheckResponseDto;
import com.example.cep.Favorite.dto.FavoriteResponseDto;
import com.example.cep.Favorite.dto.FavoriteSearchRequestDto;
import com.example.cep.common.PageDto;
import com.example.cep.util.enums.ConvenienceClassification;
import org.springframework.data.domain.Page;

public interface FavoriteRepositoryQuery {
  Page<FavoriteResponseDto> getFavorites(Long userId, PageDto pageDto);
  void deleteFavoritesByProductNameAndMore(Long userId, String productName, ConvenienceClassification convenienceClassification,String eventClassification);
  Page<FavoriteCheckResponseDto> getFavoritesAndCheck(Long userId, PageDto pageDto, FavoriteSearchRequestDto favoriteSearchRequestDto);
  Page<FavoriteCheckResponseDto> getFavoritesDuringEvent(Long userId, PageDto pageDto, FavoriteSearchRequestDto favoriteSearchRequestDto);
  Page<FavoriteCheckResponseDto> getFavoritesEventEnd(Long userId, PageDto pageDto, FavoriteSearchRequestDto favoriteSearchRequestDto);
}
