package com.kob.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println("password1");
        System.out.println(passwordEncoder.encode("password1"));
        System.out.println(passwordEncoder.matches("password1", "$2a$10$W0vwGe4/waRik06Bd.KqpunJ4BnYgR99McvfZYT3.8.0oL4H4jd8S"));
    }

}
