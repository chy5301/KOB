package com.kob.backend.service.pk;

import com.alibaba.fastjson2.JSONObject;

public interface StartGameService {
    JSONObject startGame(Integer player1Id, Integer player1BotId, Integer player2Id, Integer player2BotId);
}
