package com.example.cep.admin;

import com.example.cep.common.StatusResponseDto;
import com.example.cep.user.dto.UserRequestDto;
import com.example.cep.util.enums.ConvenienceClassification;
import org.springframework.web.bind.annotation.RequestParam;

public interface AdminService {
  StatusResponseDto requestProductsCrawl(ConvenienceClassification convenienceClassification);
}
