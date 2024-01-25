package com.kob.backend.controller.pk;

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

    @PostMapping("/pk/start/game")
    public String startGame(@RequestParam MultiValueMap<String, String> params) {
        Integer player1Id = Integer.parseInt(Objects.requireNonNull(params.getFirst("player1_id")));
        Integer player2Id = Integer.parseInt(Objects.requireNonNull(params.getFirst("player2_id")));
        return startGameService.startGame(player1Id, player2Id);
    }
}
