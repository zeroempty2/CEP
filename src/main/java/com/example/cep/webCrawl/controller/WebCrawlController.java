package com.example.cep.webCrawl.controller;

import com.example.cep.webCrawl.service.interfaces.WebCrawlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crawl")
public class WebCrawlController {
  private final WebCrawlService webCrawlService;
  @GetMapping
  public String crawl(@RequestParam String url) {
    return webCrawlService.crawl(url);
  }
}
