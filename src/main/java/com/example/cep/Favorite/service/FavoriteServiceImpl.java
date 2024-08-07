package com.example.cep.Favorite.service;

import com.example.cep.Favorite.dto.FavoriteRequestDto;
import com.example.cep.Favorite.entity.Favorite;
import com.example.cep.Favorite.repository.FavoriteRepository;
import com.example.cep.Favorite.service.interfaces.FavoriteService;
import com.example.cep.common.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
  private final FavoriteRepository favoriteRepository;

  @Override
  @Transactional
  public StatusResponseDto addFavorite(FavoriteRequestDto requestDto, Long userId) {
    Favorite favorite = Favorite.builder()
        .userId(userId)
        .productName(requestDto.productName())
        .convenienceClassification(requestDto.convenienceClassification())
        .productImg(requestDto.productImg())
        .build();
    favoriteRepository.save(favorite);
    return new StatusResponseDto(201,"Created");
  }
}
