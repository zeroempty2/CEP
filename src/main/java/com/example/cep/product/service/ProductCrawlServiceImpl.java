package com.example.cep.product.service;

import com.example.cep.common.StatusResponseDto;
import com.example.cep.product.entity.Product;
import com.example.cep.product.repository.ProductRepository;
import com.example.cep.product.service.interfaces.ProductCrawlService;
import com.example.cep.util.enums.ConvenienceClassification;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${chrome.drive.location}")
  private String location;

  private final ProductRepository productRepository;

  @Override
  @Transactional
  public StatusResponseDto crawlCuProducts() {

    String url = "https://cu.bgfretail.com/event/plus.do";

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--disable-gpu");
    options.addArguments("--remote-debugging-port=9222");

    if(location.equals("/home/ec2-user/chromedriver-linux64/chromedriver")){
      System.setProperty("webdriver.chrome.logfile", "/home/ec2-user/chromedriver-linux64/chromedriver.log");
      System.setProperty("webdriver.chrome.verboseLogging", "true");
    }

    System.setProperty("webdriver.chrome.driver", location);

    // 1 + 1 크롤링
    WebDriver driver = new ChromeDriver(options);
    try {
      crawlCuProductsByType(driver, url, "eventInfo_02");
    } catch (Exception e) {
      e.printStackTrace();
      return new StatusResponseDto(400, "bad_request");
    } finally {
      driver.quit();
    }
    System.out.println("CU 1+1 finish");

    // 2 + 1 크롤링
    driver = new ChromeDriver(options);
    try {
      crawlCuProductsByType(driver, url, "eventInfo_03");
    } catch (Exception e) {
      e.printStackTrace();
      return new StatusResponseDto(400, "bad_request");
    } finally {
      driver.quit();
    }
    System.out.println("CU 2+1 finish");

    return new StatusResponseDto(200, "success");
  }

  private void crawlCuProductsByType(WebDriver driver, String url, String eventTypeCssSelector) throws Exception {
    driver.get(url);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180));
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(180));  // 페이지 로드 대기시간
    WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#contents > div.depth3Lnb > ul > li." + eventTypeCssSelector)));
    button.click();
    Thread.sleep(10000);
    int page = 0;
    LocalDateTime start = LocalDateTime.now();
    while (true) {
      try {
        WebElement moreButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#contents > div.relCon > div > div > div.prodListBtn-w")));
        moreButton.click();
        page++;
        Thread.sleep(5000);
      } catch (Exception e) {
        break;
      }
    }
    System.out.println("totalPage: " + page +  "  startTime: " + start + "  finishTime: " + LocalDateTime.now());
    Document document = Jsoup.parse(driver.getPageSource());
    List<Product> products = parsingCUElements(document);
    int batchSize = 250;
    saveProductsInBatches(products, batchSize);
  }

  @Override
  @Transactional
  public StatusResponseDto crawlGsProducts() {
    String url = "http://gs25.gsretail.com/gscvs/ko/products/event-goods";

    ChromeOptions options = new ChromeOptions();

    options.addArguments("--headless");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--disable-gpu"); // GPU 가속 비활성화
    options.addArguments("--remote-debugging-port=9222");// 디버깅 포트 지정

    if(location.equals("/home/ec2-user/chromedriver-linux64/chromedriver")){
      System.setProperty("webdriver.chrome.logfile", "/home/ec2-user/chromedriver-linux64/chromedriver.log");
      System.setProperty("webdriver.chrome.verboseLogging", "true");
    }

    System.setProperty("webdriver.chrome.driver", location);

    WebDriver driver = new ChromeDriver(options);

    try {
      driver.get(url);

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180));
      driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(180));

      WebElement totalButton = driver.findElement(By.linkText("전체"));
      wait.until(ExpectedConditions.elementToBeClickable(totalButton)).click();
      Thread.sleep(10000);
      LocalDateTime start = LocalDateTime.now();
      List<Product> products = new ArrayList<>();
      int page = 0;
      while (true) {
        try {
          WebElement nextPageLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[1]/div[4]/div[2]/div[3]/div/div/div[4]/div/a[3]")));
          String onclickAttribute = nextPageLink.getAttribute("onclick");

          if (onclickAttribute == null || onclickAttribute.isEmpty()) {
            break; // "onclick" 속성이 없으면 반복 종료
          }
          ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextPageLink);

          Thread.sleep(8000);

          Document document = Jsoup.parse(driver.getPageSource());
          List<Product> productList = parsingGsElements(document);
          products.addAll(productList);

          page++;
        } catch (Exception e) {
          break;
        }
      }
      System.out.println("totalPage: " + page +  "  startTime: " + start + "  finishTime: " + LocalDateTime.now());
      Document document = Jsoup.parse(driver.getPageSource());
      List<Product> productList = parsingGsElements(document);
      products.addAll(productList);

      int batchSize = 250;
      saveProductsInBatches(products, batchSize);
    }  catch (Exception e) {
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

    ChromeOptions options = new ChromeOptions();

    options.addArguments("--headless");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.addArguments("--disable-gpu"); // GPU 가속 비활성화
    options.addArguments("--remote-debugging-port=9222");// 디버깅 포트 지정

    if(location.equals("/home/ec2-user/chromedriver-linux64/chromedriver")){
      System.setProperty("webdriver.chrome.logfile", "/home/ec2-user/chromedriver-linux64/chromedriver.log");
      System.setProperty("webdriver.chrome.verboseLogging", "true");
    }

    System.setProperty("webdriver.chrome.driver", location);

    WebDriver driver = new ChromeDriver(options);

    try {
      driver.get(url);

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180));
      driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(180));

      List<Product> products = new ArrayList<>();

      while (true) {
        try {
          Document document = Jsoup.parse(driver.getPageSource());
          List<Product> productList =  parsingEmartElements(document);
          products.addAll(productList);

          String currentPageSource = driver.getPageSource();

          WebElement nextPageLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div/div/div[2]/div[1]/img")));
          ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextPageLink);

          Thread.sleep(8000);

          String newPageSource = driver.getPageSource();
          if (currentPageSource.equals(newPageSource)) break;

        } catch (Exception e) {
          break;
        }
      }

      Document document = Jsoup.parse(driver.getPageSource());
      List<Product> productList = parsingEmartElements(document);
      products.addAll(productList);

      int batchSize = 100; // 배치 크기 설정
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

  private List<Product> parsingCUElements(Document document){
    Elements productListWraps = document.select("#wrap #contents .relCon .prodListWrap");

    List<Product> products = new ArrayList<>();
    HashSet<String> prodNames = new HashSet<>();
    for (Element productListWrap : productListWraps) {
      // 'ul' 요소를 선택합니다.
      Elements ulElements = productListWrap.select("ul");

      for (Element ulElement : ulElements) {
        // 'li' 요소를 선택합니다.
        Elements liElements = ulElement.select("li");

        for (Element liElement : liElements) {
          if(!prodNames.contains(liElement.select(".prod_text .name").text())){
            prodNames.add(liElement.select(".prod_text .name").text());
          }else{
            continue;
          }
          String productImg = liElement.select(".prod_img").attr("src");
          String productName = liElement.select(".prod_text .name").text();
          String productPrice = liElement.select(".prod_text .price").text();
          String productBadge1 = liElement.select(".badge .plus1").text();
          String productBadge2 = liElement.select(".badge .plus2").text();

          Product product = Product.builder()
              .productImg(productImg)
              .productPrice(productPrice)
              .productName(productName)
              .eventClassification(productBadge1.isEmpty() ? productBadge2 : productBadge1)
              .dumName("")
              .dumImg("")
              .convenienceClassification(ConvenienceClassification.CU)
              .build();

          products.add(product);
        }
      }
    }
    return products;
  }

  private List<Product> parsingGsElements(Document document) {
    Elements productListWraps = document.select("#wrap > div.cntwrap > div.cnt > div.cnt_section.mt50 > div > div > div:nth-child(9)");
    List<Product> productList = new ArrayList<>();
    HashSet<String> prodNames = new HashSet<>();
    for (Element productListWrap : productListWraps) {
      Elements ulElements = productListWrap.select("ul");
      for (Element ulElement : ulElements) {
        Elements liElements = ulElement.select("li");
        for (Element liElement : liElements) {
          if(!prodNames.contains(liElement.select(".prod_box .tit").text())){
            prodNames.add(liElement.select(".prod_box .tit").text());
          }else{
            continue;
          }
          String productImg = liElement.select(".prod_box .img img").attr("src");
          String productName = liElement.select(".prod_box .tit").text();
          String productPrice = liElement.select(".prod_box .price .cost").text().split("원")[0];
          String productBadge1 = liElement.select(".prod_box .flag_box .flg01 span").text();
          String dumName = liElement.select(".prod_box .dum_txt .name").text();
          String dumImg = liElement.select(".prod_box .dum_prd .img img").attr("src");

          Product product = Product.builder()
              .productImg(productImg)
              .productPrice(productPrice)
              .productName(productName)
              .eventClassification(productBadge1)
              .dumName(dumName)
              .dumImg(dumImg)
              .convenienceClassification(ConvenienceClassification.GS25)
              .build();

          productList.add(product);
        }
      }
    }

    return productList;
  }

  private List<Product> parsingEmartElements(Document document) {
    Elements productListWraps = document.select(".viewContentsWrap .mainContents .itemList");
    return productListWraps.stream()
        .flatMap(productListWrap -> productListWrap.select(".itemWrap").stream())
        .map(WrapElement -> {
          String productImg = WrapElement.select(".itemSpImg img").attr("src");
          String productName = WrapElement.select(".itemTxtWrap .itemtitle a").text();
          String productPrice = WrapElement.select(".itemTxtWrap span .price").text();
          String[] badgeClasses = {"onepl", "twopl", "sale", "dum"};
          String dumImg = WrapElement.select(".dumgift img").attr("src");

          String productBadge = java.util.Arrays.stream(badgeClasses)
              .map(badgeClass -> WrapElement.select(".itemTit span." + badgeClass).text())
              .filter(text -> !text.isEmpty())
              .findFirst()
              .orElse("");

          if (productBadge.isEmpty()) {
            return null;
          }

          if (productBadge.equals("1 + 1"))  productBadge = "1+1";
          else if (productBadge.equals("2 + 1"))  productBadge = "2+1";

          return Product.builder()
              .productName(productName)
              .productPrice(productPrice)
              .eventClassification(productBadge)
              .convenienceClassification(ConvenienceClassification.EMART24)
              .productImg(productImg)
              .dumImg(dumImg)
              .dumName("")
              .build();
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}
