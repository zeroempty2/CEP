package com.example.cep.favoriteQueryTest;

import com.example.cep.Favorite.dto.FavoriteCheckResponseDto;
import com.example.cep.Favorite.dto.FavoriteSearchRequestDto;
import com.example.cep.Favorite.repository.FavoriteRepositoryQueryImpl;
import com.example.cep.common.PageDto;
import com.example.cep.util.enums.ConvenienceClassification;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;


import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
@Transactional
public class FavoriteRepositoryQueryImplTest {

  @Mock
  //mocking된 객체
  private FavoriteRepositoryQueryImpl favoriteRepositoryQueryImpl;



  @Test
  //단위 테스트
  public void testGetFavoritesDuringEvent() {
    // Given
    List<ConvenienceClassification> convenienceClassifications = Arrays.asList(
        ConvenienceClassification.CU, ConvenienceClassification.GS25, ConvenienceClassification.EMART24);
    List<String> eventClassifications = Arrays.asList("1+1", "2+1");

    PageDto pageDto = mock(PageDto.class);
    when(pageDto.getPage()).thenReturn(0);
    when(pageDto.getSize()).thenReturn(20);

    FavoriteSearchRequestDto searchRequestDto = mock(FavoriteSearchRequestDto.class);
    when(searchRequestDto.convenienceClassifications()).thenReturn(convenienceClassifications);
    when(searchRequestDto.eventClassifications()).thenReturn(eventClassifications);

    Long userId = 1L;

    // Mock 데이터 생성
   List<FavoriteCheckResponseDto> mockProductList = Arrays.asList(
        new FavoriteCheckResponseDto(null,"Product1", null,null,null,null,ConvenienceClassification.CU, "1+1",null),
        new FavoriteCheckResponseDto(null,"Product2", null,null,null,null,ConvenienceClassification.GS25,"2+1", null),
        new FavoriteCheckResponseDto(null,"Product3", null,null,null,null,ConvenienceClassification.EMART24, "1+1",null)
    );

    Page<FavoriteCheckResponseDto>mockPage = new PageImpl<>(mockProductList);


    when(favoriteRepositoryQueryImpl.getFavoritesDuringEvent(1L,pageDto, searchRequestDto)).thenReturn(mockPage);

    // When
    Page<FavoriteCheckResponseDto> result = favoriteRepositoryQueryImpl.getFavoritesDuringEvent(
        userId, pageDto, searchRequestDto);

    // Then
    boolean[] convenienceFlags = new boolean[convenienceClassifications.size()];
    boolean[] eventFlags = new boolean[eventClassifications.size()];

    // 결과 리스트를 순회하며 각각의 조건 확인
    for (FavoriteCheckResponseDto favoriteDto : result.getContent()) {

      // 편의점 분류 확인
      for (int i = 0; i < convenienceClassifications.size(); i++) {
        if (favoriteDto.getConvenienceClassification().equals(convenienceClassifications.get(i))) {
          convenienceFlags[i] = true;
        }
      }

      // 행사 분류 확인
      for (int i = 0; i < eventClassifications.size(); i++) {
        if (favoriteDto.getEventClassification().equals(eventClassifications.get(i))) {
          eventFlags[i] = true;
        }
      }
    }

    // 조건으로 제공된 모든 편의점과 행사 분류가 존재하는지 확인
    boolean allConveniencePresent = true;
    boolean allEventPresent = true;

    // 모든 플래그가 true인지 확인
    for (boolean flag : convenienceFlags) {
      if (!flag) {
        allConveniencePresent = false;
        break;
      }
    }

    for (boolean flag : eventFlags) {
      if (!flag) {
        allEventPresent = false;
        break;
      }
    }

    assertTrue(allConveniencePresent);
    assertTrue(allEventPresent);
  }


}
