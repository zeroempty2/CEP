package com.example.cep.Favorite.repository;

import com.example.cep.Favorite.dto.FavoriteResponseDto;
import com.example.cep.common.PageDto;
import org.springframework.data.domain.Page;

public interface FavoriteRepositoryQuery {
Page<FavoriteResponseDto> getFavorites(Long userId, PageDto pageDto);
}
