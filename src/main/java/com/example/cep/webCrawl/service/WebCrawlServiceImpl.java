package com.example.cep.webCrawl.service;

import com.example.cep.webCrawl.service.interfaces.WebCrawlService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WebCrawlServiceImpl implements WebCrawlService {

  public String crawl(String url) {
    StringBuilder result = new StringBuilder();

    // 크롬 드라이버 경로 설정
    System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");

    // WebDriver 인스턴스 생성
    WebDriver driver = new ChromeDriver();

    try {
      // WebDriver를 사용하여 페이지 로드
      driver.get(url);

      // 페이지 소스를 가져와서 Jsoup으로 파싱
      Document document = Jsoup.parse(driver.getPageSource());

      // 원하는 요소 선택
      Elements productListWraps = document.select("#wrap #contents .relCon .prodListWrap");

      for (Element productListWrap : productListWraps) {
        Elements ulElements = productListWrap.select("ul");

        for (Element ulElement : ulElements) {
          Elements liElements = ulElement.select("li");

          for (Element liElement : liElements) {
            String productImg = liElement.select(".prod_img").text();
            String productName = liElement.select(".prod_text .name").text();
            String productPrice = liElement.select(".prod_text .price").text();

            result.append("Product Name: ").append(productName).append("\n");
            result.append("Product Price: ").append(productPrice).append("\n");
            result.append("Product Image: ").append(productImg).append("\n\n");
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return "Failed to retrieve content";
    } finally {
      // WebDriver 종료
      driver.quit();
    }

    return result.toString();
  }
}
