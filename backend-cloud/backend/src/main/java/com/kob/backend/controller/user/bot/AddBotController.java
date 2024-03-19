package com.kob.backend.controller.user.bot;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.user.bot.AddBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AddBotController {
    private final AddBotService addBotService;

    @Autowired
    public AddBotController(AddBotService addBotService) {
        this.addBotService = addBotService;
    }

    @PostMapping("/user/bot/add")
    public JSONObject addBot(@RequestParam Map<String, String> params) {
        return addBotService.addBot(params);
    }
}
