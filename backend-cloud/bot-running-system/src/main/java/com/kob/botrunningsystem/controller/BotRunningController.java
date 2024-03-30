package com.kob.botrunningsystem.controller;

import com.alibaba.fastjson2.JSONObject;
import com.kob.botrunningsystem.service.BotRunningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class BotRunningController {
    private final BotRunningService botRunningService;

    @Autowired
    public BotRunningController(BotRunningService botRunningService) {
        this.botRunningService = botRunningService;
    }

    @PostMapping("/api/bot/add-task")
    public JSONObject addBot(@RequestParam MultiValueMap<String, String> params) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(params.getFirst("user_id")));
        String botCode = params.getFirst("bot_code");
        String gameInfo = params.getFirst("game_info");
        return botRunningService.addBot(userId, botCode, gameInfo);
    }
}
