package com.example.cep.Favorite.controller;

import com.example.cep.Favorite.dto.FavoriteCheckResponseDto;
import com.example.cep.Favorite.dto.FavoriteRequestDto;
import com.example.cep.Favorite.dto.FavoriteResponseDto;
import com.example.cep.Favorite.dto.FavoriteSearchRequestDto;
import com.example.cep.Favorite.service.interfaces.FavoriteService;
import com.example.cep.common.PageDto;
import com.example.cep.common.StatusResponseDto;
import com.example.cep.security.UserDetailsImpl;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

  @PostMapping("/request")
  public ResponseEntity<StatusResponseDto> requestFavorite(@RequestBody FavoriteRequestDto requestDto,@AuthenticationPrincipal
  UserDetailsImpl userDetails){
    StatusResponseDto statusResponseDto = favoriteService.requestFavorite(requestDto, userDetails.getUserId());
    return ResponseEntity.ok().body(statusResponseDto);
  }

  @GetMapping
  public ResponseEntity<Page<FavoriteResponseDto>> getFavorite(@AuthenticationPrincipal UserDetailsImpl userDetails,
      PageDto pageDto) {
    Page<FavoriteResponseDto> favorites = favoriteService.getFavorites(userDetails.getUserId(),pageDto);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
    return ResponseEntity.ok().headers(headers).body(favorites);
  }

  @PostMapping("/check")
  public ResponseEntity<Page<FavoriteCheckResponseDto>> getCheckingFavorite(@AuthenticationPrincipal UserDetailsImpl userDetails,
      PageDto pageDto, @RequestBody FavoriteSearchRequestDto favoriteSearchRequestDto) {
    Page<FavoriteCheckResponseDto> favorites = favoriteService.getCheckingFavorite(userDetails.getUserId(),pageDto,favoriteSearchRequestDto);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
    return ResponseEntity.ok().headers(headers).body(favorites);
  }


  @DeleteMapping
  public ResponseEntity<StatusResponseDto> deleteFavorite(@AuthenticationPrincipal UserDetailsImpl userDetails,Long favoriteId) {
    if(!favoriteService.deleteFavorite(userDetails.getUserId(),favoriteId)) return ResponseEntity.badRequest().build();
    else return ResponseEntity.noContent().build();
  }
}
