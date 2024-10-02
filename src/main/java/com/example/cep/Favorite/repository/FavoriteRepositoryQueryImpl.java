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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    List<String> productHashes = favorites.stream()
        .map(FavoriteResponseDto::getProductHash)
        .collect(Collectors.toList());

    List<Product> products = productService.findByProductHashIn(productHashes);

    Set<String> existingProductHashes = products.stream()
        .map(Product::getProductHash)
        .collect(Collectors.toSet());

    List<FavoriteResponseDto> filteredFavorites = favorites.stream()
        .filter(fav -> existingProductHashes.contains(fav.getProductHash()))
        .collect(Collectors.toList());

    List<FavoriteCheckResponseDto> favoriteCheckList = filteredFavorites.stream()
        .map(fav -> {
          // Find the corresponding product for the favorite
          Product matchingProduct = products.stream()
              .filter(prod -> prod.getProductHash().equals(fav.getProductHash()))
              .findFirst()
              .orElse(null);

          return new FavoriteCheckResponseDto(
              fav.getFavoriteId(),
              fav.getProductName(),
              fav.getProductImg(),
              matchingProduct != null ? matchingProduct.getProductPrice() : "가격정보 없음", // productPrice
              fav.getDumName() != null ? fav.getDumName() : "", // dumName
              fav.getDumImg() != null ? fav.getDumImg() : "",   // dumImg
              fav.getConvenienceClassification(),
              fav.getEventClassification(),
              true
          );
        })
        .collect(Collectors.toList());

    long totalSize = countQuery(userId).fetch().get(0);

    return PageableExecutionUtils.getPage(favoriteCheckList, pageable, () -> totalSize);
  }

  @Override
  @Transactional
  public Page<FavoriteCheckResponseDto> getFavoritesEventEnd(Long userId, PageDto pageDto,
      FavoriteSearchRequestDto favoriteSearchRequestDto) {
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

    List<String> productHashes = favorites.stream()
        .map(FavoriteResponseDto::getProductHash)
        .collect(Collectors.toList());

    List<Product> products = productService.findByProductHashIn(productHashes);

    Set<String> existingProductHashes = products.stream()
        .map(Product::getProductHash)
        .collect(Collectors.toSet());

    List<FavoriteResponseDto> filteredFavorites = favorites.stream()
        .filter(fav -> existingProductHashes.contains(fav.getProductHash()))
        .collect(Collectors.toList());

    List<FavoriteCheckResponseDto> favoriteCheckList =  filteredFavorites.stream()
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

    long totalSize = countQuery(userId).fetch().get(0);

    return PageableExecutionUtils.getPage(favoriteCheckList, pageable, () -> totalSize);
  }


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
