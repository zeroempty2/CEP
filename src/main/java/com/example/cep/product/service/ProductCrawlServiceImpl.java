package com.example.cep.product.service;

import com.example.cep.common.StatusResponseDto;
import com.example.cep.product.entity.Product;
import com.example.cep.product.repository.ProductRepository;
import com.example.cep.product.service.interfaces.ProductCrawlService;
import com.example.cep.util.enums.ConvenienceClassification;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCrawlServiceImpl implements ProductCrawlService {
  private final ProductRepository productRepository;

  @Override
  @Transactional
  public StatusResponseDto crawlCuProducts() {
    String url = "https://cu.bgfretail.com/event/plus.do";
    //크롬 드라이버 경로지정
    System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");

    WebDriver driver = new ChromeDriver();

    try{
      driver.get(url);

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
      driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));  //첫 페이지 로드 대기시간

      while (true) {
        try {
          //더보기 버튼 클릭
          WebElement moreButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".prodListBtn .prodListBtn-w")));
          moreButton.click();
          //버튼 클릭 후 대기시간
          Thread.sleep(10000);
        } catch (Exception e) {
          break;
        }
      }

      Document document = Jsoup.parse(driver.getPageSource());

      Elements productListWraps = document.select("#wrap #contents .relCon .prodListWrap");




      List<Product> products = productListWraps.stream()
          .flatMap(productListWrap -> productListWrap.select("ul").stream())
          .flatMap(ulElement -> ulElement.select("li").stream())
          .map(liElement -> {
            String productImg = liElement.select(".prod_img").attr("src");
            String productName = liElement.select(".prod_text .name").text();
            String productPrice = liElement.select(".prod_text .price").text();
            String productBadge1 = liElement.select(".badge .plus1").text();
            String productBadge2 = liElement.select(".badge .plus2").text();
            return Product.builder()
                .productImg(productImg)
                .productPrice(productPrice)
                .productName(productName)
                .eventClassification((productBadge1.isEmpty() ? productBadge2 : productBadge1))
                .convenienceClassification(ConvenienceClassification.CU)
                .build();
          })
          .collect(Collectors.toList());

      int batchSize = 250; // 배치 크기 설정
      saveProductsInBatches(products, batchSize);

    }
    catch (Exception e) {
      e.printStackTrace();
      return new StatusResponseDto(400, "bad_request");
    }
    finally {
      driver.quit();
    }

    return new StatusResponseDto(200, "success");
  }

  @Override
  @Transactional
  public StatusResponseDto crawlGsProducts() {
    String url = "http://gs25.gsretail.com/gscvs/ko/products/event-goods";

    System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");

    WebDriver driver = new ChromeDriver();

    try {
      driver.get(url);

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
      driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));

      WebElement totalButton = driver.findElement(By.linkText("전체"));
      wait.until(ExpectedConditions.elementToBeClickable(totalButton)).click();

      Thread.sleep(10000);

      List<Product> products = new ArrayList<>();

      while (true) {
        try {
          Document document = Jsoup.parse(driver.getPageSource());
          List<Product> productList = parsingGsElements(document,ConvenienceClassification.GS);
          products.addAll(productList);

          String currentPageSource = driver.getPageSource();

          WebElement nextPageLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div[4]/div[2]/div[3]/div/div/div[4]/div/a[3]")));
          ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextPageLink);

          Thread.sleep(5000);

          String newPageSource = driver.getPageSource();
          if (currentPageSource.equals(newPageSource)) break;

        } catch (Exception e) {
          break;
        }
      }

      Document document = Jsoup.parse(driver.getPageSource());
      List<Product> productList = parsingGsElements(document,ConvenienceClassification.GS);
      products.addAll(productList);

      int batchSize = 250; // 배치 크기 설정
      saveProductsInBatches(products, batchSize);
    }
    catch (Exception e) {
      e.printStackTrace();
      return new StatusResponseDto(400, "bad_request");
    }
    finally {
      driver.quit();
    }

    return new StatusResponseDto(200, "success");
  }

  @Override
  @Transactional
  public StatusResponseDto crawlEmartProducts() {
    String url = "https://www.emart24.co.kr/goods/event";

    System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");

    WebDriver driver = new ChromeDriver();

    try {
      driver.get(url);

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
      driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));

      List<Product> products = new ArrayList<>();

      while (true) {
        try {
          Document document = Jsoup.parse(driver.getPageSource());
          List<Product> productList =  parsingEmartElements(document,ConvenienceClassification.EMART);
          products.addAll(productList);

          String currentPageSource = driver.getPageSource();

          WebElement nextPageLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div/div/div[2]/div[1]/img")));
          ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextPageLink);

          Thread.sleep(5000);

          String newPageSource = driver.getPageSource();
          if (currentPageSource.equals(newPageSource)) break;

        } catch (Exception e) {
          break;
        }
      }

      Document document = Jsoup.parse(driver.getPageSource());
      List<Product> productList = parsingEmartElements(document,ConvenienceClassification.EMART);
      products.addAll(productList);

      int batchSize = 250; // 배치 크기 설정
      saveProductsInBatches(products, batchSize);
    }
    catch (Exception e) {
      e.printStackTrace();
      return new StatusResponseDto(400, "bad_request");
    }
    finally {
      driver.quit();
    }

    return new StatusResponseDto(200, "success");
  }

  private void saveProductsInBatches(List<Product> products, int batchSize) {
    for (int i = 0; i < products.size(); i += batchSize) {
      int end = Math.min(i + batchSize, products.size());
      List<Product> batch = products.subList(i, end);
      productRepository.saveAll(batch);
    }
  }

  private List<Product> parsingGsElements(Document document,ConvenienceClassification convenienceClassification) {
    Elements productListWraps = document.select("#wrap > div.cntwrap > div.cnt > div.cnt_section.mt50 > div > div > div:nth-child(9)");
    return productListWraps.stream()
        .flatMap(productListWrap -> productListWrap.select("ul").stream())
        .flatMap(ulElement -> ulElement.select("li").stream())
        .map(liElement -> {
          String productImg = liElement.select(".prod_box .img img").attr("src");
          String productName = liElement.select(".prod_box .tit").text();
          String productPrice = liElement.select(".prod_box .price .cost").text();
          String productBadge1 = liElement.select(".prod_box .flag_box .flg01 span").text();
          return Product.builder()
              .productImg(productImg)
              .productPrice(productPrice)
              .productName(productName)
              .eventClassification(productBadge1)
              .convenienceClassification(convenienceClassification)
              .build();
        })
        .collect(Collectors.toList());
  }

  private List<Product> parsingEmartElements(Document document,ConvenienceClassification convenienceClassification) {
    Elements productListWraps = document.select(".viewContentsWrap .mainContents .itemList");
    return productListWraps.stream()
        .flatMap(productListWrap -> productListWrap.select(".itemWrap").stream())
        .map(WrapElement -> {
          String productImg = WrapElement.select(".itemSpImg img").attr("src");
          String productName = WrapElement.select(".itemTxtWrap .itemtitle a").text();
          String productPrice = WrapElement.select(".itemTxtWrap span .price").text();
          String[] badgeClasses = {"onepl", "twopl", "salepl", "dum", "gola"};
          String productBadge = java.util.Arrays.stream(badgeClasses)
              .map(badgeClass -> WrapElement.select(".itemTit span." + badgeClass).text())
              .filter(text -> !text.isEmpty())
              .findFirst()
              .orElse("");

          return Product.builder()
              .productName(productName)
              .productPrice(productPrice)
              .eventClassification(productBadge)
              .convenienceClassification(convenienceClassification)
              .productImg(productImg)
              .build();
        })
        .collect(Collectors.toList());
  }
}
