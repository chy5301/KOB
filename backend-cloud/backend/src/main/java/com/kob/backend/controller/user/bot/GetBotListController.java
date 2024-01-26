package com.kob.backend.controller.user.bot;

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

    @GetMapping("/user/bot/getlist")
    public Map<String, Object> getBotList() {
        return getBotListService.getBotList();
    }
}
