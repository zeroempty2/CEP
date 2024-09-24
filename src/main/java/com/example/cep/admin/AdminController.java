package com.example.cep.admin;

import com.example.cep.common.StatusResponseDto;
import com.example.cep.product.repository.ProductRepository;
import com.example.cep.util.enums.ConvenienceClassification;
import com.example.cep.util.scheduler.AccessControl;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//권한에 대한 인가는 시큐리티에서 통과했다고 생각하고 검증하지 않는다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
  private final AccessControl accessControl;
  private final ProductRepository productRepository;
  private final AdminService adminService;

  @PostMapping("/block")
  public StatusResponseDto apiBlock(){
    accessControl.blockAccess();
    return new StatusResponseDto(200,"block_success");
  }

  @PostMapping("/allow")
  public StatusResponseDto apiAllow(){
    accessControl.allowAccess();
    return new StatusResponseDto(200,"allow_success");
  }

  @PostMapping("/crawl")
    public StatusResponseDto requestProductsCrawl(@RequestParam ConvenienceClassification convenienceClassification){
    return adminService.requestProductsCrawl(convenienceClassification);
  }

  @DeleteMapping("/delete")
  @Transactional
  public StatusResponseDto deleteProducts(@RequestParam ConvenienceClassification convenienceClassification){
    productRepository.deleteAllByConvenienceClassification(convenienceClassification);
    return new StatusResponseDto(200,"success");
  }



}
