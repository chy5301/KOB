package com.kob.backend.controller.user.account;

import com.kob.backend.service.impl.user.account.LoginServiceImpl;
import com.kob.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/user/account/token")
    public Map<String, String> getToken(@RequestParam Map<String, String> paramMap) {
        String username = paramMap.get("username");
        String password = paramMap.get("password");
        return loginService.getToken(username, password);
    }
}
