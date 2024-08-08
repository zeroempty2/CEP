package com.example.cep.Favorite.service;

import com.example.cep.Favorite.dto.FavoriteRequestDto;
import com.example.cep.Favorite.dto.FavoriteResponseDto;
import com.example.cep.Favorite.entity.Favorite;
import com.example.cep.Favorite.repository.FavoriteRepository;
import com.example.cep.Favorite.service.interfaces.FavoriteService;
import com.example.cep.common.PageDto;
import com.example.cep.common.StatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

  @Override
  @Transactional(readOnly = true)
  public Page<FavoriteResponseDto> getFavorites(Long userId, PageDto pageDto) {
    return favoriteRepository.getFavorites(userId,pageDto);
  }

  @Override
  public Boolean deleteFavorite(Long userId, Long favoriteId) {
    if(findFavoriteById(favoriteId).isOwner(userId)) favoriteRepository.deleteById(favoriteId);
    else return false;

    return true;
  }

  @Override
  public Favorite findFavoriteById(Long favoriteId) {
    return favoriteRepository.findById(favoriteId)
        .orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 Id입니다"));
  }


}
