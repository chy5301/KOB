package com.kob.backend.controller.user.account;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.user.account.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {
    private final InfoService infoService;

    @Autowired
    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/api/user/account/info")
    public JSONObject getInfo() {
        return infoService.getInfo();
    }
}
