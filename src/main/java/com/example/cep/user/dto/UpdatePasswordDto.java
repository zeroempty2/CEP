package com.example.cep.user.dto;

import com.example.cep.util.customAnnotaion.validatePassword.ValidatePassword;

public record UpdatePasswordDto(@ValidatePassword String newPassword, String oldPassword) {

}
