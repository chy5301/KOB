package com.kob.backend.controller.pk;

import com.alibaba.fastjson2.JSONObject;
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

    @PostMapping("/api/pk/receive/bot/move")
    public JSONObject receiveBotMove(@RequestParam MultiValueMap<String, String> params) {
        return receiveBotMoveService.receiveBotMove(params);
    }
}
