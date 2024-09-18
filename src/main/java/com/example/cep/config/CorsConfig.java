package com.example.cep.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
//    config.addAllowedOrigin("http://localhost:3000");
    config.addAllowedOrigin("https://eventprod.store");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.setExposedHeaders(Arrays.asList("Authorization"));
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}
