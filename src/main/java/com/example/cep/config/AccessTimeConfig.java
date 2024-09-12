package com.example.cep.config;

import com.example.cep.util.scheduler.AccessControl;
import com.example.cep.util.scheduler.TimeBasedFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

//커스텀 필터 적용
@Configuration
public class AccessTimeConfig {
  @Bean
  public FilterRegistrationBean<TimeBasedFilter> timeBasedFilterRegistration(
      AccessControl accessControl) {
    FilterRegistrationBean<TimeBasedFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new TimeBasedFilter(accessControl));
    registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
    registrationBean.addUrlPatterns("/*");
    return registrationBean;
  }
}
