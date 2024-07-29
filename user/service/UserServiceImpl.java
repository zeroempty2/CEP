package com.example.cep.user.service;



import com.example.cep.common.StatusResponseDto;
import com.example.cep.user.dto.UserLoginRequestDto;
import com.example.cep.user.dto.UserProfileResponseDto;
import com.example.cep.user.dto.UserRequestDto;
import com.example.cep.user.entity.User;
import com.example.cep.user.repository.UserRepository;
import com.example.cep.util.JwtUtil;
import com.example.cep.util.enums.UserRoleEnum;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Override //Transactional 넣어주세요
  @Transactional
  public StatusResponseDto signUp(UserRequestDto userRequestDto) {

    if(userRepository.existsByUsername(userRequestDto.username())) return new StatusResponseDto(400,"Already exist!");

    String encodedPwd = passwordEncoder.encode(userRequestDto.password());

    User user = User.builder()
        .username(userRequestDto.username())
        .password(encodedPwd)
        .phoneNumber(userRequestDto.phoneNumber())
        .aboutMe(userRequestDto.aboutMe())
        .nickName(userRequestDto.nickName())
        .profileUrl(userRequestDto.profileUrl())
        .role(UserRoleEnum.CUSTOMER)
        .build();

    userRepository.save(user);


    return new StatusResponseDto(201,"created!");
  }

  @Override
  public StatusResponseDto login(HttpServletResponse httpServletResponse,
      UserLoginRequestDto userLoginRequestDto) {

    Optional<User> user = userRepository.findByUsername(userLoginRequestDto.getUsername());

    if(user.isEmpty()) return new StatusResponseDto(401,"user is Not exist!");

    if(!passwordEncoder.matches(userLoginRequestDto.getPassword(),user.get().getPassword())) return new StatusResponseDto(401,"password is not matches!");

    String accessToken = jwtUtil.createAccessToken(user.get().getUsername(),user.get().getRole());
    //httpServletResponse.addCookie(accessToken);
    httpServletResponse.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken); //header나 cookie는 백에서 발급, local 저장은 프론트에서 가능


    return new StatusResponseDto(201,"success!");
  }

  @Override
  public User findUserByUserId(Long userId) {
    return userRepository.findById(userId).orElseThrow(
        () -> new IllegalArgumentException("유효하지 않은 Id입니다")
    );

  }

  @Override
  @Transactional(readOnly = true)
  public UserProfileResponseDto getUserProfile(Long userId) {
    User user = findUserByUserId(userId);
    return new UserProfileResponseDto(user.getNickName(),user.getPhoneNumber(),user.getAboutMe(),user.getProfileUrl());
  }
}
