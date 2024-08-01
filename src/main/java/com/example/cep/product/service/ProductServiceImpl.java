package com.example.cep.product.service;

import com.example.cep.common.StatusResponseDto;
import com.example.cep.product.entity.Product;
import com.example.cep.product.repository.ProductRepository;
import com.example.cep.product.service.interfaces.ProductService;
import com.example.cep.util.enums.ConvenienceClassification;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;

  @Override
  @Transactional
  public StatusResponseDto crawlCuProducts() {
    String url = "https://cu.bgfretail.com/event/plus.do";
    System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");

    WebDriver driver = new ChromeDriver();

    try {
      driver.get(url);

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));
      driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

      while (true) {
        try {
          WebElement moreButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".prodListBtn .prodListBtn-w")));
          moreButton.click();
          Thread.sleep(5000);
        } catch (Exception e) {
          break;
        }
      }

      Document document = Jsoup.parse(driver.getPageSource());

      Elements productListWraps = document.select("#wrap #contents .relCon .prodListWrap");

      List<Product> products = new ArrayList<>();
      int batchSize = 100; // 배치 크기 설정

      for (Element productListWrap : productListWraps) {
        Elements ulElements = productListWrap.select("ul");

        for (Element ulElement : ulElements) {
          Elements liElements = ulElement.select("li");

          for (Element liElement : liElements) {
            String productImg = liElement.select(".prod_img").attr("src");
            String productName = liElement.select(".prod_text .name").text();
            String productPrice = liElement.select(".prod_text .price").text();
            String productBadge1 = liElement.select(".badge .plus1").text();
            String productBadge2 = liElement.select(".badge .plus2").text();
            Product product = Product.builder()
                .productImg(productImg)
                .productPrice(productPrice)
                .productName(productName)
                .eventClassification((productBadge1.isEmpty() ? productBadge2 : productBadge1))
                .convenienceClassification(ConvenienceClassification.CU)
                .build();
            products.add(product);

            if (products.size() >= batchSize) {
              productRepository.saveAll(products);
              products.clear();
            }
          }
        }
      }

      if (!products.isEmpty()) {
        productRepository.saveAll(products);
      }

    } catch (Exception e) {
      e.printStackTrace();
      return new StatusResponseDto(400, "bad_request");
    } finally {
      driver.quit();
    }

    return new StatusResponseDto(200, "success");
  }
}
