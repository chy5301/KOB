package com.kob.backend.service.user.bot;

import com.alibaba.fastjson2.JSONObject;

import java.util.Map;

public interface RemoveBotService {
    JSONObject removeBot(Map<String, String> data);
}
