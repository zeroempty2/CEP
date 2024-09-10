package com.example.cep.Favorite.repository;

import static com.example.cep.Favorite.entity.QFavorite.favorite;


import com.example.cep.Favorite.dto.FavoriteCheckResponseDto;
import com.example.cep.Favorite.dto.FavoriteResponseDto;
import com.example.cep.common.PageDto;
import com.example.cep.product.entity.Product;
import com.example.cep.product.service.interfaces.ProductService;
import com.example.cep.util.enums.ConvenienceClassification;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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

    long totalSize = countQuery().fetch().get(0);

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
  public Page<FavoriteCheckResponseDto> getFavoritesAndCheck(Long userId, PageDto pageDto) {
    Pageable pageable = pageDto.toPageable();

    List<FavoriteResponseDto> favorites = query(userId)
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


    List<FavoriteCheckResponseDto> favoriteCheckList = favorites.stream().map(fav -> {

      Product matchingProduct = products.stream()
          .filter(prod -> prod.getProductName().equals(fav.getProductName())
              && prod.getEventClassification().equals(fav.getEventClassification())
              && prod.getConvenienceClassification().equals(fav.getConvenienceClassification()))
          .findFirst()
          .orElse(null);

      return new FavoriteCheckResponseDto(
          fav.getFavoriteId(),
          fav.getProductName(),
          fav.getProductImg(),
          matchingProduct != null ? matchingProduct.getProductPrice() : "가격정보 없음",
          fav.getConvenienceClassification(),
          fav.getEventClassification(),
          matchingProduct != null // 일치하는 product가 있으면 isSale = true
      );
    }).collect(Collectors.toList());


    long totalSize = countQuery().fetch().get(0);

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
            )
        )
        .from(favorite)
        .where(favorite.userId.eq(userId));

  }
  private JPAQuery<Long> countQuery() {
    return jpaQueryFactory.select(Wildcard.count)
        .from(favorite);
  }
}
