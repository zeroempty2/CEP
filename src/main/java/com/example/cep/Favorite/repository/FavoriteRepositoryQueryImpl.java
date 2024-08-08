package com.example.cep.Favorite.repository;

import static com.example.cep.Favorite.entity.QFavorite.favorite;


import com.example.cep.Favorite.dto.FavoriteResponseDto;
import com.example.cep.common.PageDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class FavoriteRepositoryQueryImpl implements FavoriteRepositoryQuery {
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Page<FavoriteResponseDto> getFavorites(Long userId, PageDto pageDto) {
    Pageable pageable = pageDto.toPageable();

    List<FavoriteResponseDto> list =
        query(userId)
            .orderBy(favorite.createdAt.desc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
            .fetch();

    long totalSize = countQuery().fetch().get(0);

    return PageableExecutionUtils.getPage(list, pageable, () -> totalSize);
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
