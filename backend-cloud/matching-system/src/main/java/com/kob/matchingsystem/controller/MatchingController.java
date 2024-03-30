package com.kob.matchingsystem.controller;

import com.alibaba.fastjson2.JSONObject;
import com.kob.matchingsystem.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class MatchingController {
    private final MatchingService matchingService;

    @Autowired
    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @PostMapping("/api/player/add")
    public JSONObject addPlayer(@RequestParam MultiValueMap<String, String> params) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(params.getFirst("user_id")));
        Integer rating = Integer.parseInt(Objects.requireNonNull(params.getFirst("rating")));
        Integer botId = Integer.parseInt(Objects.requireNonNull(params.getFirst("bot_id")));
        return matchingService.addPlayer(userId, rating, botId);
    }

    @PostMapping("/api/player/remove")
    public JSONObject removePlayer(@RequestParam MultiValueMap<String, String> params) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(params.getFirst("user_id")));
        return matchingService.removePlayer(userId);
    }
}
