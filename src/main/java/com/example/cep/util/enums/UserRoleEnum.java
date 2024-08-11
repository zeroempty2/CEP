package com.example.cep.util.enums;

public enum UserRoleEnum {
  USER(Authority.USER),
  ADMINISTRATOR(Authority.ADMINISTRATOR);



  private final String authority;

  UserRoleEnum(String authority) {
    this.authority = authority;
  }

  public String getAuthority() {
    return this.authority;
  }

  public static class Authority {
    public static final String USER = "ROLE_USER";
    public static final String ADMINISTRATOR = "ROLE_ADMINISTRATOR";
  }
}

