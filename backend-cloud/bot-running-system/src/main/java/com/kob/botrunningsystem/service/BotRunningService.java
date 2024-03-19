package com.kob.botrunningsystem.service;

import com.alibaba.fastjson2.JSONObject;

public interface BotRunningService {
    JSONObject addBot(Integer userId, String botCode, String gameInfo);
}
