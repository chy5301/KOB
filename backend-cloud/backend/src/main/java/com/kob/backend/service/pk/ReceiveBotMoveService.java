package com.kob.backend.service.pk;

import com.alibaba.fastjson2.JSONObject;

public interface ReceiveBotMoveService {
    JSONObject receiveBotMove(Integer userId, Integer direction);
}
