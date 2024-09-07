package com.example.cep.user.repository;

import static com.example.cep.user.entity.QUser.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryQueryImpl implements UserRepositoryQuery{
  private final JPAQueryFactory jpaQueryFactory;


  @Override
  public boolean existsByValidationContents(String validationContents, String validationClassification) {
    PathBuilder<Object> entityPath = new PathBuilder<>(Object.class, "user");
    StringPath columnPath = entityPath.getString(validationClassification);

    BooleanExpression condition = columnPath.eq(validationContents);

    return jpaQueryFactory.from(user)
        .where(condition)
        .setHint("org.hibernate.readOnly", true)
        .fetchFirst() != null;
  }

  @Override
  public boolean existsByUsername(String username) {
    return jpaQueryFactory.from(user)
        .where(user.username.eq(username))
        .setHint("org.hibernate.readOnly", true)
        .fetchFirst() != null;
  }
}

