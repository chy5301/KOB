package com.kob.backend.service.impl.pk;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public JSONObject receiveBotMove(Integer userId, Integer direction) {
        WebSocketServer.users.get(userId).moveByBot(direction);
        JSONObject returnInfo = new JSONObject();
        returnInfo.put("status_message", "Success");
        return returnInfo;
    }
}
