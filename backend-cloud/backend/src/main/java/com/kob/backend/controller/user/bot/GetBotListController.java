package com.kob.backend.controller.user.bot;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.user.bot.GetBotListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
public class GetBotListController {
    private final GetBotListService getBotListService;

    @Autowired
    public GetBotListController(GetBotListService getBotListService) {
        this.getBotListService = getBotListService;
    }

    @GetMapping("/api/user/bot/list")
    public JSONObject getBotList() {
        return getBotListService.getBotList();
    }
}
