package com.kob.backend.controller.user.bot;

import com.kob.backend.service.user.bot.RemoveBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RemoveBotController {

    @Autowired
    private RemoveBotService removeBotService;

    @PostMapping("/user/bot/remove")
    public Map<String, String> removeBot(@RequestParam Map<String, String> params) {
        return removeBotService.removeBot(params);
    }
}
