package com.example.cep.util.scheduler;


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

  private final AccessControl accessControl;

  public TimeBasedFilter(AccessControl accessControl) {
    this.accessControl = accessControl;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String requestURI = httpRequest.getRequestURI();

    if (requestURI.startsWith("/products/crawl/")) {
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
