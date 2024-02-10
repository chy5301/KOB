package com.kob.backend.controller.pk;

import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ReceiveBotMoveController {
    private final ReceiveBotMoveService receiveBotMoveService;

    @Autowired
    public ReceiveBotMoveController(ReceiveBotMoveService receiveBotMoveService) {
        this.receiveBotMoveService = receiveBotMoveService;
    }

    @PostMapping("/pk/receive/bot/move")
    public String receiveBotMove(@RequestParam MultiValueMap<String, String> params) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(params.getFirst("user_id")));
        Integer direction = Integer.parseInt(Objects.requireNonNull(params.getFirst("direction")));
        return receiveBotMoveService.receiveBotMove(userId, direction);
    }
}
