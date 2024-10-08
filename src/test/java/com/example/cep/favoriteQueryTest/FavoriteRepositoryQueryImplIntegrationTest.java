package com.example.cep.favoriteQueryTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.cep.Favorite.dto.FavoriteCheckResponseDto;
import com.example.cep.Favorite.dto.FavoriteSearchRequestDto;
import com.example.cep.Favorite.repository.FavoriteRepositoryQueryImpl;
import com.example.cep.common.PageDto;
import com.example.cep.util.enums.ConvenienceClassification;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class FavoriteRepositoryQueryImplIntegrationTest {
  @Autowired
  private FavoriteRepositoryQueryImpl favoriteRepositoryQueryImpl;

  @Test
  //실제 요청 반환
  //일정 조건의 검색조건을 제공한 후, 제공한 조건에 맞는 데이터 확인시 테스트 성공
  //이벤트가 끝난 상품 조회(products에 존재하지 않는 상품 조회)
  public void testGetFavoritesEndEvent() {
    // Given
    //검색조건 제공
    Long userId = 2L; // 테스트용 유저 ID(임의로 조작된 특정 데이터가 들어간 테스트데이터)
    List<ConvenienceClassification> convenienceClassifications = Arrays.asList(
        ConvenienceClassification.CU, ConvenienceClassification.EMART24);
    List<String> eventClassifications = Arrays.asList("1+1", "2+1");

    // 20개의 데이터를 가져오도록 설정
    PageDto pageDto = new PageDto(0, 100, false, null);

    // 다른 조건은 제외하고 편의점, 행사상품 종류만 탐색
    FavoriteSearchRequestDto searchRequestDto = new FavoriteSearchRequestDto(
        null, // keyword
        convenienceClassifications, // convenienceClassifications
        eventClassifications // eventClassifications
    );

    // When
    Page<FavoriteCheckResponseDto> result = favoriteRepositoryQueryImpl.getFavoritesEventEnd(
        userId, pageDto, searchRequestDto);

    // Then
    assertNotNull(result); // 결과가 null이 아닌지 확인

    boolean[] convenienceFlags = new boolean[convenienceClassifications.size()];
    boolean[] eventFlags = new boolean[eventClassifications.size()];

    // 결과 리스트를 순회하며 각각의 조건 확인
    for (FavoriteCheckResponseDto favoriteDto : result.getContent()) {
      // 편의점 분류 확인
      for (int i = 0; i < convenienceClassifications.size(); i++) {
        if (favoriteDto.getConvenienceClassification()
            .equals(convenienceClassifications.get(i))) {
          convenienceFlags[i] = true; // 해당 편의점 분류가 존재하면 플래그 true
        }
      }

      // 행사 분류 확인
      for (int i = 0; i < eventClassifications.size(); i++) {
        if (favoriteDto.getEventClassification().equals(eventClassifications.get(i))) {
          eventFlags[i] = true; // 해당 행사 분류가 존재하면 플래그 true
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
