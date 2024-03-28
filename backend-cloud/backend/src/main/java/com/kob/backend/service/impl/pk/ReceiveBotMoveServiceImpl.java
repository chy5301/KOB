package com.kob.backend.service.impl.pk;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Objects;

@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {
    @Override
    public JSONObject receiveBotMove(MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        if ("Success".equals(data.getFirst("status_message"))) {
            Integer direction = Integer.parseInt(Objects.requireNonNull(data.getFirst("direction")));
            WebSocketServer.users.get(userId).moveByBot(direction);
        } else {
            String exceptionClass = data.getFirst("exception_class");
            String exceptionMessage = data.getFirst("exception_message");
            WebSocketServer.users.get(userId).runningException(exceptionClass, exceptionMessage);
        }
        JSONObject returnInfo = new JSONObject();
        returnInfo.put("status_message", "Success");
        return returnInfo;
    }
}
