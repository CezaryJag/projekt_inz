package com.example.demo.sheduler;

import com.example.demo.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final UserService userService;

    public ScheduledTasks(UserService userService) {
        this.userService = userService;
    }

    // Harmonogram: uruchamiaj co godzinę
    @Scheduled(fixedRate = 1200000) // 3600000 ms = 1 godzina
    public void cleanUpExpiredAccounts() {
        userService.removeExpiredUnverifiedAccounts();
    }
}

