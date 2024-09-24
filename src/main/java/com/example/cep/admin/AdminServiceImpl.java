package com.example.cep.admin;


import com.example.cep.common.StatusResponseDto;
import com.example.cep.product.service.interfaces.ProductCrawlService;
import com.example.cep.product.service.interfaces.ProductService;
import com.example.cep.util.enums.ConvenienceClassification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
  private final ProductCrawlService productCrawlService;

  @Override
  public StatusResponseDto requestProductsCrawl(
      ConvenienceClassification convenienceClassification) {
    if(convenienceClassification.equals(ConvenienceClassification.CU)){
      productCrawlService.crawlCuProducts();
    } else if (convenienceClassification.equals(ConvenienceClassification.GS25)) {
      productCrawlService.crawlGsProducts();
    }else if(convenienceClassification.equals(ConvenienceClassification.EMART24)){
      productCrawlService.crawlEmartProducts();
    }
    return new StatusResponseDto(200,"OK");
  }
}
