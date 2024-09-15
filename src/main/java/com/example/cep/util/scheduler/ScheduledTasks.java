package com.example.cep.util.scheduler;

import com.example.cep.product.controller.ProductController;
import com.example.cep.product.controller.ProductCrawlController;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTasks {
  private final ProductCrawlController productCrawlController;
  private final ProductController productController;
  @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
  public void runApiWithAdministratorRole() {
    UserDetails adminUser = getAdminUser();
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(adminUser, null,
            Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")));

    // SecurityContextHolder에 인증 정보 설정
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    callApi();

    // 이후 SecurityContext를 초기화
    SecurityContextHolder.clearContext();
  }

  // ADMINISTRATOR 사용자의 UserDetails 생성
  public UserDetails getAdminUser() {
    return new org.springframework.security.core.userdetails.User(
        "스케줄러", "", Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")));
  }

  public void callApi() {
    productController.deleteAllProducts();
    productCrawlController.crawlCuProducts();
    productCrawlController.crawlGsProducts();
    productCrawlController.crawlEmartProducts();
  }
}
