package com.example.cep.user.dto;

import com.example.cep.util.customAnnotaion.validateEmail.ValidateEmail;
import com.example.cep.util.customAnnotaion.validateNickname.ValidateNickname;
import com.example.cep.util.customAnnotaion.validatePassword.ValidatePassword;
import com.example.cep.util.customAnnotaion.validateUsername.ValidateUsername;

public record UserRequestDto (@ValidateUsername String username, @ValidatePassword String password, @ValidateEmail String email){


}
