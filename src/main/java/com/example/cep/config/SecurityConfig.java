package com.example.cep.config;

import com.example.cep.security.CustomAccessDeniedHandler;
import com.example.cep.security.CustomAuthenticationEntryPoint;
import com.example.cep.security.JwtAuthFilter;
import com.example.cep.security.UserDetailsServiceImpl;
import com.example.cep.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final JwtUtil jwtUtil;
  private final UserDetailsServiceImpl userDetailsService;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring()
        .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//        .requestMatchers(PathRequest.toH2Console())
//        .requestMatchers("/users/sign");

  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .addFilterBefore(new JwtAuthFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/favorite/**").hasRole("CUSTOMER")
            .requestMatchers("/products/**").permitAll()
            .requestMatchers("/products/crawl/**").permitAll()
            .requestMatchers("/users/**").permitAll()
            .anyRequest().authenticated()
        )  .exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(customAuthenticationEntryPoint) // 인증 실패 시 처리할 핸들러 지정
            .accessDeniedHandler(customAccessDeniedHandler) // 권한 부족 시 처리할 핸들러 지정
        );

    return http.build();
  }


}
