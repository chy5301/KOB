package com.kob.backend.service.impl.pk;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public JSONObject startGame(Integer player1Id, Integer player1BotId, Integer player2Id, Integer player2BotId) {
        WebSocketServer.startGame(player1Id, player1BotId, player2Id, player2BotId);
        JSONObject returnInfo = new JSONObject();
        returnInfo.put("status_message", "Success");
        return returnInfo;
    }
}
