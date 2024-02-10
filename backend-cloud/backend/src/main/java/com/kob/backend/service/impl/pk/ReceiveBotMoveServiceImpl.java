package com.kob.backend.service.impl.pk;

import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        // 输出调试信息
        System.out.println("Receive bot move: " + direction + " created by user: " + userId);
        WebSocketServer.users.get(userId).moveByBot(direction);
        return "Receive bot move success";
    }
}
