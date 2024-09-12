package com.example.cep.util.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AccessControl {

  private boolean accessAllowed = true;

  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void blockAccess() {
    accessAllowed = false;
  }

  @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
  public void allowAccess() {
    accessAllowed = true;
  }

  public boolean isAccessAllowed() {
    return accessAllowed;
  }

}
