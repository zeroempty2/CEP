//package com.example.cep.boot;
//
//
//import java.io.IOException;
//import lombok.RequiredArgsConstructor;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class WebCrawlerApplication implements CommandLineRunner {
//
//  public static void main(String[] args) {
//    SpringApplication.run(WebCrawlerApplication.class, args);
//  }
//
//  @Override
//  public void run(String... args) throws Exception {
//    String url = "https://cu.bgfretail.com/event/plus.do";
//    try {
//      Document doc = Jsoup.connect(url).get();
//      Elements titles = doc.select("h2.title");
//      for (Element title : titles) {
//        System.out.println(title.text());
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
//}
