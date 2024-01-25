package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.StartGameService;
import org.springframework.stereotype.Service;

@Service
public class StartGameServiceImpl implements StartGameService {
    @Override
    public String startGame(Integer player1Id, Integer player2Id) {
        // 输出调试信息
        System.out.println("Start game: player1Id = " + player1Id + " player2Id = " + player2Id);
        WebSocketServer.startGame(player1Id, player2Id);
        return "Start game success";
    }
}
