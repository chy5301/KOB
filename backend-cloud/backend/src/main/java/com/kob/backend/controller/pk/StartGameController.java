package com.kob.backend.controller.pk;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.pk.StartGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class StartGameController {
    private final StartGameService startGameService;

    @Autowired
    public StartGameController(StartGameService startGameService) {
        this.startGameService = startGameService;
    }

    @PostMapping("/api/pk/start/game")
    public JSONObject startGame(@RequestParam MultiValueMap<String, String> params) {
        Integer player1Id = Integer.parseInt(Objects.requireNonNull(params.getFirst("player1_id")));
        Integer player1BotId = Integer.parseInt(Objects.requireNonNull(params.getFirst("player1_bot_id")));
        Integer player2Id = Integer.parseInt(Objects.requireNonNull(params.getFirst("player2_id")));
        Integer player2BotId = Integer.parseInt(Objects.requireNonNull(params.getFirst("player2_bot_id")));
        return startGameService.startGame(player1Id, player1BotId, player2Id, player2BotId);
    }
}
