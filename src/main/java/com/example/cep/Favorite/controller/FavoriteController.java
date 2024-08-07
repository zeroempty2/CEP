package com.example.cep.Favorite.controller;

import com.example.cep.Favorite.dto.FavoriteRequestDto;
import com.example.cep.Favorite.service.interfaces.FavoriteService;
import com.example.cep.common.StatusResponseDto;
import com.example.cep.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite")
public class FavoriteController {
  private final FavoriteService favoriteService;

  @PostMapping
  public ResponseEntity<StatusResponseDto> addFavorite(@RequestBody FavoriteRequestDto requestDto,@AuthenticationPrincipal
      UserDetailsImpl userDetails) {
    StatusResponseDto statusResponseDto = favoriteService.addFavorite(requestDto, userDetails.getUserId());
    return ResponseEntity.ok().body(statusResponseDto);
  }
}
