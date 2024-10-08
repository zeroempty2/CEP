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
import java.util.Collections;
import java.util.List;
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

    List<FavoriteCheckResponseDto> favoriteCheckList = jpaQueryFactory
        .select(
            Projections.bean(
                FavoriteCheckResponseDto.class,
                favorite.id.as("favoriteId"),
                favorite.productName,
                favorite.productImg,
                product.productPrice,
                favorite.dumName,
                favorite.dumImg,
                favorite.convenienceClassification,
                favorite.eventClassification
            )
        )
        .from(favorite)
        .leftJoin(product).on(favorite.productHash.eq(product.productHash))
        .where(builder)
        .orderBy(favorite.id.desc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();

    favoriteCheckList.forEach(dto -> dto.setIsSale(true));

    // 전체 크기 계산
    long totalSize = favoriteCheckList.size();

    return new PageImpl<>(favoriteCheckList, pageable, totalSize);
  }
  
  @Override
  @Transactional
  public Page<FavoriteCheckResponseDto> getFavoritesEventEnd(Long userId, PageDto pageDto,
      FavoriteSearchRequestDto favoriteSearchRequestDto) {
    Pageable pageable = pageDto.toPageable();
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

    // Favorite 데이터를 우선 조회
    List<FavoriteCheckResponseDto> favoriteCheckList = jpaQueryFactory
        .select(
            Projections.bean(
                FavoriteCheckResponseDto.class,
                favorite.id.as("favoriteId"),
                favorite.productName,
                favorite.productImg,
                product.productPrice,
                favorite.dumName,
                favorite.dumImg,
                favorite.convenienceClassification,
                favorite.eventClassification
            )
        )
        .from(favorite)
        .leftJoin(product).on(product.productHash.eq(favorite.productHash)) // 일치하는 productHash 조건으로 조인
        .where(builder)
        .orderBy(favorite.id.desc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();

    // product가 없는 favorite 필터링 (leftJoin에서 product가 없으면 null이므로 이를 기반으로 필터링)
    favoriteCheckList = favoriteCheckList.stream()
        .filter(dto -> dto.getProductPrice() == null) // product가 조인되지 않은 경우만 필터링
        .toList();

    // 페이징 인덱스 설정
    int startIndex;

    if(pageDto.getPage() == 0){
      startIndex = 0;
    }else{
      startIndex = (pageDto.getPage() - 1) * pageSize;
    }

    int endIndex = Math.min(startIndex + pageSize, favoriteCheckList.size());

    if (startIndex >= favoriteCheckList.size()) {
      // 시작 인덱스가 전체 리스트 크기보다 크면 빈 리스트 반환
      return new PageImpl<>(Collections.emptyList(), pageable, 0);
    }

    // 필요한 데이터만 추출
    List<FavoriteCheckResponseDto> pagedFavorites = favoriteCheckList.subList(startIndex, endIndex);

    pagedFavorites.forEach(dto -> dto.setIsSale(false));

    // 전체 크기 계산
    long totalSize = favoriteCheckList.size();

    return new PageImpl<>(pagedFavorites, pageable, totalSize);
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
