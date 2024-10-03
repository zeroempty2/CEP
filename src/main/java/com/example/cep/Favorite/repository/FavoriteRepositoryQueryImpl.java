package com.example.cep.Favorite.repository;

import static com.example.cep.Favorite.entity.QFavorite.favorite;
import static com.example.cep.product.entity.QProduct.product;


import com.example.cep.Favorite.dto.FavoriteCheckResponseDto;
import com.example.cep.Favorite.dto.FavoriteResponseDto;
import com.example.cep.Favorite.dto.FavoriteSearchRequestDto;
import com.example.cep.common.PageDto;
import com.example.cep.product.entity.Product;
import com.example.cep.product.service.interfaces.ProductService;
import com.example.cep.util.enums.ConvenienceClassification;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class FavoriteRepositoryQueryImpl implements FavoriteRepositoryQuery {
  private final JPAQueryFactory jpaQueryFactory;
  private final ProductService productService;

  @Override
  public Page<FavoriteResponseDto> getFavorites(Long userId, PageDto pageDto) {
    Pageable pageable = pageDto.toPageable();

    List<FavoriteResponseDto> list =
        query(userId)
            .orderBy(favorite.id.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    long totalSize = countQuery(userId).fetch().get(0);

    return PageableExecutionUtils.getPage(list, pageable, () -> totalSize);
  }

  @Override
  public void deleteFavoritesByProductNameAndMore(Long userId, String productName,
      ConvenienceClassification convenienceClassification, String eventClassification) {
    jpaQueryFactory
        .delete(favorite)
        .where(favorite.userId.eq(userId)
            .and(favorite.productName.eq(productName))
            .and(favorite.eventClassification.eq(eventClassification))
            .and(favorite.convenienceClassification.eq(convenienceClassification))
        )
        .execute();
  }

  @Override
  @Transactional
  public Page<FavoriteCheckResponseDto> getFavoritesAndCheck(Long userId, PageDto pageDto, FavoriteSearchRequestDto favoriteSearchRequestDto) {
    Pageable pageable = pageDto.toPageable();

    BooleanBuilder builder = new BooleanBuilder();

    builder.and(favorite.userId.eq(userId));

    if (favoriteSearchRequestDto.keyword() != null) {
      builder.and(favorite.productName.containsIgnoreCase(favoriteSearchRequestDto.keyword()));
    }

    if (favoriteSearchRequestDto.convenienceClassifications() != null && !favoriteSearchRequestDto.convenienceClassifications().isEmpty()) {
      builder.and(favorite.convenienceClassification.in(favoriteSearchRequestDto.convenienceClassifications()));
    }

    if (favoriteSearchRequestDto.eventClassifications() != null && !favoriteSearchRequestDto.eventClassifications().isEmpty()) {
      builder.and(favorite.eventClassification.in(favoriteSearchRequestDto.eventClassifications()));
    }

    List<FavoriteResponseDto> favorites =jpaQueryFactory
        .select(
            Projections.bean(
                FavoriteResponseDto.class
                ,favorite.id.as("favoriteId")
                ,favorite.productName
                ,favorite.productImg
                ,favorite.convenienceClassification
                ,favorite.eventClassification
                ,favorite.productHash
                ,favorite.dumName
                ,favorite.dumImg
            )
        )
        .from(favorite)
        .where(builder)
        .orderBy(favorite.id.desc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();


    List<String> productNames = favorites.stream()
        .map(FavoriteResponseDto::getProductName)
        .collect(Collectors.toList());

    List<String> eventClassifications = favorites.stream()
        .map(FavoriteResponseDto::getEventClassification)
        .collect(Collectors.toList());

    List<ConvenienceClassification> convenienceClassifications = favorites.stream()
        .map(FavoriteResponseDto::getConvenienceClassification)
        .collect(Collectors.toList());


    List<Product> products = productService.findByProductNameInAndEventClassificationInAndConvenienceClassificationIn(
        productNames, eventClassifications, convenienceClassifications
    );


    List<FavoriteCheckResponseDto> favoriteCheckList = matchingProduct(products,favorites);

    long totalSize = countQuery(userId).fetch().get(0);

    return PageableExecutionUtils.getPage(favoriteCheckList, pageable, () -> totalSize);
  }

  @Override
  @Transactional
  public Page<FavoriteCheckResponseDto> getFavoritesDuringEvent(Long userId, PageDto pageDto,
      FavoriteSearchRequestDto favoriteSearchRequestDto) {
    Pageable pageable = pageDto.toPageable();
    int currentPage = favoriteSearchRequestDto.currentPage();
    int pageSize = pageDto.getSize();

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(favorite.userId.eq(userId));

    if (favoriteSearchRequestDto.keyword() != null) {
      builder.and(favorite.productName.containsIgnoreCase(favoriteSearchRequestDto.keyword()));
    }

    if (favoriteSearchRequestDto.convenienceClassifications() != null && !favoriteSearchRequestDto.convenienceClassifications().isEmpty()) {
      builder.and(favorite.convenienceClassification.in(favoriteSearchRequestDto.convenienceClassifications()));
    }

    if (favoriteSearchRequestDto.eventClassifications() != null && !favoriteSearchRequestDto.eventClassifications().isEmpty()) {
      builder.and(favorite.eventClassification.in(favoriteSearchRequestDto.eventClassifications()));
    }

    // favorite 테이블에서 일괄적으로 데이터 가져오기 (batchSize 없이 전부 가져옴)
    List<FavoriteResponseDto> allFavorites = jpaQueryFactory
        .select(
            Projections.bean(
                FavoriteResponseDto.class,
                favorite.id.as("favoriteId"),
                favorite.productName,
                favorite.productImg,
                favorite.convenienceClassification,
                favorite.eventClassification,
                favorite.productHash,
                favorite.dumName,
                favorite.dumImg
            )
        )
        .from(favorite)
        .where(builder)
        .fetch();

    if (allFavorites.isEmpty()) {
      // 데이터가 없으면 빈 페이지 반환
      return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    // 가져온 모든 favorite 데이터에서 ProductHash추출
    List<String> productHashes = allFavorites.stream()
        .map(FavoriteResponseDto::getProductHash)
        .collect(Collectors.toList());

    // 추출한 ProductHash를 이용해 product탐색(판매중인 product탐색)
    List<Product> products = productService.findByProductHashIn(productHashes);

    //판매중인 product의 produchHash 추출
    Set<String> existingProductHashes = products.stream()
        .map(Product::getProductHash)
        .collect(Collectors.toSet());

    // 판매중인 product의 productHash를 가지고 있는 favorite만 필터링
    List<FavoriteResponseDto> filteredFavorites = allFavorites.stream()
        .filter(fav -> existingProductHashes.contains(fav.getProductHash()))
        .collect(Collectors.toList());

    // 페이징 인덱스 설정
    int startIndex = (currentPage - 1) * pageSize;
    int endIndex = Math.min(startIndex + pageSize, filteredFavorites.size());

    if (startIndex >= filteredFavorites.size()) {
      // 시작 인덱스가 전체 리스트 크기보다 크면 빈 리스트 반환
      return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    // 필요한 데이터만 추출
    List<FavoriteResponseDto> pagedFavorites = filteredFavorites.subList(startIndex, endIndex);

    // Product 정보를 사용해 FavoriteCheckResponseDto로 변환
    List<FavoriteCheckResponseDto> favoriteCheckList = pagedFavorites.stream()
        .map(fav -> {
          Product matchingProduct = products.stream()
              .filter(product -> product.getProductHash().equals(fav.getProductHash()))
              .findFirst()
              .orElse(null);

          return new FavoriteCheckResponseDto(
              fav.getFavoriteId(),
              fav.getProductName(),
              fav.getProductImg(),
              matchingProduct != null ? matchingProduct.getProductPrice() : "가격정보 없음",
              fav.getDumName() != null ? fav.getDumName() : "",
              fav.getDumImg() != null ? fav.getDumImg() : "",
              fav.getConvenienceClassification(),
              fav.getEventClassification(),
              true
          );
        })
        .collect(Collectors.toList());

    // 전체 크기 계산
    long totalSize = filteredFavorites.size();

    return new PageImpl<>(favoriteCheckList, pageable, totalSize);
  }
  
  @Override
  @Transactional
  public Page<FavoriteCheckResponseDto> getFavoritesEventEnd(Long userId, PageDto pageDto,
      FavoriteSearchRequestDto favoriteSearchRequestDto) {
    Pageable pageable = pageDto.toPageable();
    int currentPage = favoriteSearchRequestDto.currentPage();
    int pageSize = pageDto.getSize();

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(favorite.userId.eq(userId));

    if (favoriteSearchRequestDto.keyword() != null) {
      builder.and(favorite.productName.containsIgnoreCase(favoriteSearchRequestDto.keyword()));
    }

    if (favoriteSearchRequestDto.convenienceClassifications() != null && !favoriteSearchRequestDto.convenienceClassifications().isEmpty()) {
      builder.and(favorite.convenienceClassification.in(favoriteSearchRequestDto.convenienceClassifications()));
    }

    if (favoriteSearchRequestDto.eventClassifications() != null && !favoriteSearchRequestDto.eventClassifications().isEmpty()) {
      builder.and(favorite.eventClassification.in(favoriteSearchRequestDto.eventClassifications()));
    }

    // favorite 테이블에서 일괄적으로 데이터 가져오기 (batchSize 없이 전부 가져옴)
    List<FavoriteResponseDto> allFavorites = jpaQueryFactory
        .select(
            Projections.bean(
                FavoriteResponseDto.class,
                favorite.id.as("favoriteId"),
                favorite.productName,
                favorite.productImg,
                favorite.convenienceClassification,
                favorite.eventClassification,
                favorite.productHash,
                favorite.dumName,
                favorite.dumImg
            )
        )
        .from(favorite)
        .where(builder)
        .fetch();

    if (allFavorites.isEmpty()) {
      // 데이터가 없으면 빈 페이지 반환
      return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    // 가져온 모든 favorite 데이터에서 ProductHash추출
    List<String> productHashes = allFavorites.stream()
        .map(FavoriteResponseDto::getProductHash)
        .collect(Collectors.toList());

    // 추출한 ProductHash를 이용해 product탐색(판매중인 product탐색)
    List<Product> products = productService.findByProductHashIn(productHashes);

    //판매중인 product의 produchHash 추출
    Set<String> existingProductHashes = products.stream()
        .map(Product::getProductHash)
        .collect(Collectors.toSet());

    // 판매중인 product의 productHash를 가지고 있지 않은 favorite만 필터링
    List<FavoriteResponseDto> filteredFavorites = allFavorites.stream()
        .filter(fav -> !existingProductHashes.contains(fav.getProductHash()))
        .collect(Collectors.toList());

    // 페이징 인덱스 설정
    int startIndex = (currentPage - 1) * pageSize;
    int endIndex = Math.min(startIndex + pageSize, filteredFavorites.size());

    if (startIndex >= filteredFavorites.size()) {
      // 시작 인덱스가 전체 리스트 크기보다 크면 빈 리스트 반환
      return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    // 필요한 데이터만 추출
    List<FavoriteResponseDto> pagedFavorites = filteredFavorites.subList(startIndex, endIndex);

    //pagedFavorites를 후처리
    List<FavoriteCheckResponseDto> favoriteCheckList =  pagedFavorites.stream()
        .map(fav -> new FavoriteCheckResponseDto(
            fav.getFavoriteId(),
            fav.getProductName(),
            fav.getProductImg(),
            "가격정보 없음", // productPrice
            fav.getDumName() != null ? fav.getDumName() : "", // dumName
            fav.getDumImg() != null ? fav.getDumImg() : "",   // dumImg
            fav.getConvenienceClassification(),
            fav.getEventClassification(),
            false // isSale
        ))
        .collect(Collectors.toList());

    // 전체 크기 계산
    long totalSize = filteredFavorites.size();

    return new PageImpl<>(favoriteCheckList, pageable, totalSize);
  }
//  @Override
//  @Transactional
//  public Page<FavoriteCheckResponseDto> getFavoritesEventEnd(Long userId, PageDto pageDto,
//      FavoriteSearchRequestDto favoriteSearchRequestDto) {
//    Pageable pageable = pageDto.toPageable();
//
//    BooleanBuilder builder = new BooleanBuilder();
//
//    builder.and(favorite.userId.eq(userId));
//
//    if (favoriteSearchRequestDto.keyword() != null) {
//      builder.and(favorite.productName.containsIgnoreCase(favoriteSearchRequestDto.keyword()));
//    }
//
//    if (favoriteSearchRequestDto.convenienceClassifications() != null && !favoriteSearchRequestDto.convenienceClassifications().isEmpty()) {
//      builder.and(favorite.convenienceClassification.in(favoriteSearchRequestDto.convenienceClassifications()));
//    }
//
//    if (favoriteSearchRequestDto.eventClassifications() != null && !favoriteSearchRequestDto.eventClassifications().isEmpty()) {
//      builder.and(favorite.eventClassification.in(favoriteSearchRequestDto.eventClassifications()));
//    }
//
//    List<FavoriteResponseDto> favorites =jpaQueryFactory
//        .select(
//            Projections.bean(
//                FavoriteResponseDto.class
//                ,favorite.id.as("favoriteId")
//                ,favorite.productName
//                ,favorite.productImg
//                ,favorite.convenienceClassification
//                ,favorite.eventClassification
//                ,favorite.productHash
//                ,favorite.dumName
//                ,favorite.dumImg
//            )
//        )
//        .from(favorite)
//        .where(builder)
//        .orderBy(favorite.id.desc())
//        .limit(pageable.getPageSize())
//        .offset(pageable.getOffset())
//        .fetch();
//
//    List<String> productHashes = favorites.stream()
//        .map(FavoriteResponseDto::getProductHash)
//        .collect(Collectors.toList());
//
//    List<Product> products = productService.findByProductHashIn(productHashes);
//
//    Set<String> existingProductHashes = products.stream()
//        .map(Product::getProductHash)
//        .collect(Collectors.toSet());
//
//    List<FavoriteResponseDto> filteredFavorites = favorites.stream()
//        .filter(fav -> !existingProductHashes.contains(fav.getProductHash()))
//        .collect(Collectors.toList());
//
//    List<FavoriteCheckResponseDto> favoriteCheckList =  filteredFavorites.stream()
//        .map(fav -> new FavoriteCheckResponseDto(
//            fav.getFavoriteId(),
//            fav.getProductName(),
//            fav.getProductImg(),
//            "가격정보 없음", // productPrice
//            fav.getDumName() != null ? fav.getDumName() : "", // dumName
//            fav.getDumImg() != null ? fav.getDumImg() : "",   // dumImg
//            fav.getConvenienceClassification(),
//            fav.getEventClassification(),
//            false // isSale
//        ))
//        .collect(Collectors.toList());
//
//    long totalSize = countQuery(userId).fetch().get(0);
//
//    return PageableExecutionUtils.getPage(favoriteCheckList, pageable, () -> totalSize);
//  }


  private JPAQuery<FavoriteResponseDto> query(Long userId) {
    return jpaQueryFactory
        .select(
            Projections.bean(
                FavoriteResponseDto.class
                ,favorite.id.as("favoriteId")
                ,favorite.productName
                ,favorite.productImg
                ,favorite.convenienceClassification
                ,favorite.eventClassification
                ,favorite.productHash
                ,favorite.dumName
                ,favorite.dumImg
            )
        )
        .from(favorite)
        .where(favorite.userId.eq(userId));

  }
  private JPAQuery<Long> countQuery(Long userId) {
    return jpaQueryFactory.select(Wildcard.count)
        .from(favorite).where(favorite.userId.eq(userId));
  }

  private List<FavoriteCheckResponseDto> matchingProduct(List<Product> products, List<FavoriteResponseDto> favorites) {
    return favorites.stream().map(fav -> {
      Product matchingProduct = products.stream()
          .filter(prod -> prod.getProductHash().equals(fav.getProductHash()))
          .findFirst()
          .orElse(null);

      return new FavoriteCheckResponseDto(
          fav.getFavoriteId(),
          fav.getProductName(),
          fav.getProductImg(),
          matchingProduct != null ? matchingProduct.getProductPrice() : "가격정보 없음",
          fav.getDumName() != null ? fav.getDumName() : "", // dumName
          fav.getDumImg() != null ? fav.getDumImg() : "",
          fav.getConvenienceClassification(),
          fav.getEventClassification(),
          matchingProduct != null // 일치하는 product가 있으면 isSale = true]
      );
    }).collect(Collectors.toList());
  }

}
