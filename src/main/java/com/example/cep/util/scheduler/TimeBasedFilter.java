package com.example.cep.util.scheduler;


import com.example.cep.security.UserDetailsImpl;
import com.example.cep.security.UserDetailsServiceImpl;
import com.example.cep.util.JwtUtil;
import com.example.cep.util.enums.UserRoleEnum;
import io.jsonwebtoken.Claims;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


public class TimeBasedFilter implements Filter {
  private final JwtUtil jwtUtil;
  private final AccessControl accessControl;
  private final UserDetailsServiceImpl userDetailsService;

  public TimeBasedFilter(AccessControl accessControl, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
    this.accessControl = accessControl;
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String token = jwtUtil.resolveToken(httpRequest);
    UserDetailsImpl userDetails;
    UserRoleEnum role = UserRoleEnum.USER;

    if(token != null){
      if(jwtUtil.validateToken(token)){
        Claims info = jwtUtil.getUserInfoFromToken(token);
        userDetails =  userDetailsService.loadUserDetailByUsername(info.getSubject());
        if(userDetails != null) {
          if (userDetails.getUser().getRole() != null) role = userDetails.getUser().getRole();
        }
      }
    }

    String requestURI = httpRequest.getRequestURI();

    if (requestURI.startsWith("/products/crawl/") || role == UserRoleEnum.ADMINISTRATOR) {
      chain.doFilter(request, response);
    } else {
      if (!accessControl.isAccessAllowed()) {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access is not allowed at this time.");
        return;
      }
      chain.doFilter(request, response); // 필터 로직 적용
    }
  }

  @Override
  public void destroy() {

  }


}
