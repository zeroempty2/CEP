package com.example.cep.product.repository;

import static com.example.cep.product.entity.QProduct.product;

import com.example.cep.common.PageDto;
import com.example.cep.product.dto.ProductRequestDto;
import com.example.cep.product.dto.ProductResponseDto;
import com.example.cep.util.enums.ConvenienceClassification;
import com.querydsl.core.BooleanBuilder;
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
public class ProductRepositoryQueryImpl implements ProductRepositoryQuery {
  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Page<ProductResponseDto> findProducts(PageDto pageDto,
      ProductRequestDto productRequestDto) {
    Pageable pageable = pageDto.toPageable();

    BooleanBuilder builder = new BooleanBuilder();

    if (productRequestDto.keyword() != null) {
      builder.and(product.productName.containsIgnoreCase(productRequestDto.keyword()));
    }

    if (productRequestDto.convenienceClassifications() != null && !productRequestDto.convenienceClassifications().isEmpty()) {
      builder.and(product.convenienceClassification.in(productRequestDto.convenienceClassifications()));
    }

    if (productRequestDto.eventClassifications() != null && !productRequestDto.eventClassifications().isEmpty()) {
      builder.and(product.eventClassification.in(productRequestDto.eventClassifications()));
    }

    JPAQuery<ProductResponseDto> query = jpaQueryFactory
        .select(
            Projections.bean(
                ProductResponseDto.class
                ,product.id.as("productId")
                ,product.productName
                ,product.productPrice
                ,product.productImg
                ,product.eventClassification
                ,product.convenienceClassification
            )
        )
        .from(product)
        .where(builder)
        .orderBy(product.createdAt.desc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset());

    List<ProductResponseDto> list = query.fetch();

    long totalSize = countQuery(builder).fetch().get(0);

    return PageableExecutionUtils.getPage(list, pageable, () -> totalSize);
  }

  private JPAQuery<Long> countQuery(BooleanBuilder builder) {
    return jpaQueryFactory.select(Wildcard.count)
        .from(product)
        .where(builder);
  }


  @Override
  public Page<ProductResponseDto> findAllProducts(PageDto pageDto) {
    Pageable pageable = pageDto.toPageable();

    List<ProductResponseDto> list =  jpaQueryFactory
        .select(
            Projections.bean(
                ProductResponseDto.class
                ,product.id.as("productId")
                ,product.productName
                ,product.productPrice
                ,product.productImg
                ,product.eventClassification
                ,product.convenienceClassification
            )
        )
        .from(product)
        .orderBy(product.createdAt.desc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();

    long totalSize = countQuery().fetch().get(0);

    return PageableExecutionUtils.getPage(list, pageable, () -> totalSize);
  }

  @Override
  public Page<ProductResponseDto> pagingProducts(PageDto pageDto,ConvenienceClassification convenienceClassification) {
    Pageable pageable = pageDto.toPageable();

    List<ProductResponseDto> list =
        findAllByConvenienceClassification(convenienceClassification)
        .orderBy(product.createdAt.desc())
        .limit(pageable.getPageSize())
        .offset(pageable.getOffset())
        .fetch();

    long totalSize = countQuery().fetch().get(0);

    return PageableExecutionUtils.getPage(list, pageable, () -> totalSize);
  }

  private JPAQuery<ProductResponseDto> findAllByConvenienceClassification(ConvenienceClassification convenienceClassification) {
    return jpaQueryFactory
        .select(
            Projections.bean(
                ProductResponseDto.class
                ,product.id.as("productId")
                ,product.productName
                ,product.productPrice
                ,product.productImg
                ,product.eventClassification
                ,product.convenienceClassification
            )
        )
        .from(product)
        .where(product.convenienceClassification.eq(convenienceClassification));

  }

  private JPAQuery<Long> countQuery() {
    return jpaQueryFactory.select(Wildcard.count)
        .from(product);
  }
}
