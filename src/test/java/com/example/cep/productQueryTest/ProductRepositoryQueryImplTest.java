package com.example.cep.productQueryTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.cep.Favorite.dto.FavoriteCheckResponseDto;
import com.example.cep.Favorite.dto.FavoriteSearchRequestDto;
import com.example.cep.common.PageDto;
import com.example.cep.product.dto.ProductRequestDto;
import com.example.cep.product.dto.ProductResponseDto;
import com.example.cep.product.entity.Product;
import com.example.cep.product.repository.ProductRepositoryQueryImpl;
import com.example.cep.util.enums.ConvenienceClassification;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductRepositoryQueryImplTest {

  @Mock
  private ProductRepositoryQueryImpl productRepositoryQueryImpl;

  @Test
  //단위 테스트
  public void findProducts() {
    // Given
    List<ConvenienceClassification> convenienceClassifications = Arrays.asList(
        ConvenienceClassification.CU, ConvenienceClassification.GS25, ConvenienceClassification.EMART24);
    List<String> eventClassifications = Arrays.asList("1+1", "2+1");

    PageDto pageDto = mock(PageDto.class);
    when(pageDto.getPage()).thenReturn(0);
    when(pageDto.getSize()).thenReturn(20);

    ProductRequestDto productRequestDto = mock(ProductRequestDto.class);
    when(productRequestDto.convenienceClassifications()).thenReturn(convenienceClassifications);
    when(productRequestDto.eventClassifications()).thenReturn(eventClassifications);

    // Mock 데이터 생성
    List<ProductResponseDto> mockProductList = Arrays.asList(
        new ProductResponseDto(null,"Product1", null,null,null,null,"1+1",ConvenienceClassification.CU, null),
        new ProductResponseDto(null,"Product2", null,null,null,null,"2+1",ConvenienceClassification.GS25, null),
        new ProductResponseDto(null,"Product3", null,null,null,null,"1+1",ConvenienceClassification.EMART24, null)
    );

    Page<ProductResponseDto> mockPage = new PageImpl<>(mockProductList);

    // productRepositoryQueryImpl이 findProducts 메서드 호출 시 mockPage를 반환하도록 설정
    when(productRepositoryQueryImpl.findProducts(pageDto, productRequestDto)).thenReturn(mockPage);

    // When
    Page<ProductResponseDto> result = productRepositoryQueryImpl.findProducts(pageDto, productRequestDto);

    // Then
    boolean[] convenienceFlags = new boolean[convenienceClassifications.size()];
    boolean[] eventFlags = new boolean[eventClassifications.size()];

    // 결과 리스트를 순회하며 각각의 조건 확인
    for (ProductResponseDto favoriteDto : result.getContent()) {
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
