package com.example.cep.user.dto;

import com.example.cep.util.customAnnotaion.validateUsername.ValidateUsername;

public record UsernameDuplicationCheckDto(@ValidateUsername String username) {

}
