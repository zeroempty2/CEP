package com.example.cep.user.controller;



import com.example.cep.common.StatusResponseDto;
import com.example.cep.security.UserDetailsImpl;
import com.example.cep.user.dto.UserInfoDuplicationCheckDto;
import com.example.cep.user.dto.UserLoginRequestDto;
import com.example.cep.user.dto.UserProfileResponseDto;
import com.example.cep.user.dto.UserRequestDto;
import com.example.cep.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/signUp")
  public StatusResponseDto signUp(@RequestBody UserRequestDto userRequestDto){
   return userService.signUp(userRequestDto);
  }

  @PostMapping("/login")
  public StatusResponseDto login(HttpServletResponse httpServletResponse,@RequestBody UserLoginRequestDto userLoginRequestDto){
   return userService.login(httpServletResponse,userLoginRequestDto);
  }

  @PostMapping("/check")
  public ResponseEntity<Boolean> userInfoDuplicationCheck(@RequestBody UserInfoDuplicationCheckDto userInfoDuplicationCheckDto){
    return ResponseEntity.ok().body(userService.userInfoDuplicationCheck(userInfoDuplicationCheckDto));
  }

  @GetMapping("/profile")
  public ResponseEntity<UserProfileResponseDto> getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails){
    UserProfileResponseDto userProfileResponseDto = userService.getUserProfile(userDetails.getUserId());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
    return ResponseEntity.ok().headers(headers).body(userProfileResponseDto);
  }

}
