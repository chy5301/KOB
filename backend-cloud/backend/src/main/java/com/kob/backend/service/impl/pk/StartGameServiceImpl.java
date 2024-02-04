package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer player1Id, Integer player1BotId, Integer player2Id, Integer player2BotId) {
        // 输出调试信息
        System.out.println("Start game: player1Id = " + player1Id + " player2Id = " + player2Id);
        WebSocketServer.startGame(player1Id, player1BotId, player2Id, player2BotId);
        return "Start game success";
    }
}
