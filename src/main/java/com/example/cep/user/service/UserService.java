package com.example.cep.user.service;



import com.example.cep.common.StatusResponseDto;
import com.example.cep.user.dto.UserInfoDuplicationCheckDto;
import com.example.cep.user.dto.UserLoginRequestDto;
import com.example.cep.user.dto.UserProfileResponseDto;
import com.example.cep.user.dto.UserRequestDto;
import com.example.cep.user.dto.UsernameDuplicationCheckDto;
import com.example.cep.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

  StatusResponseDto signUp(UserRequestDto userRequestDto); //회원가입

  StatusResponseDto login(HttpServletResponse httpServletResponse, UserLoginRequestDto userLoginRequestDto);

  User findUserByUserId(Long userId);
//
//  UserProfileResponseDto getUserProfile(Long userId);

  Boolean userInfoDuplicationCheck(UserInfoDuplicationCheckDto userInfoDuplicationCheckDto);

  Boolean usernameDuplicationCheck(UsernameDuplicationCheckDto usernameDuplicationCheckDto);
}
