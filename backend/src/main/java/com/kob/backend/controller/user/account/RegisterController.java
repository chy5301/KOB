package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @PostMapping("/user/account/register")
    public Map<String, String> register(@RequestParam Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String confirmedPassword = params.get("confirmed_password");
        return registerService.register(username, password, confirmedPassword);
    }
}
