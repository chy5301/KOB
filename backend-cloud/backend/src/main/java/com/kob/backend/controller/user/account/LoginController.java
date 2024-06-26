package com.kob.backend.controller.user.account;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.user.account.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {
    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/api/user/account/token")
    public JSONObject getToken(@RequestParam Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        return loginService.getToken(username, password);
    }
}
