package com.kob.matchingsystem.service;

import com.alibaba.fastjson2.JSONObject;

public interface MatchingService {
    JSONObject addPlayer(Integer userId, Integer rating, Integer botId);

    JSONObject removePlayer(Integer userId);
}
