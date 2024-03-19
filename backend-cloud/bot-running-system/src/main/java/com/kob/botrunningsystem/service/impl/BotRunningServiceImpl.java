package com.kob.botrunningsystem.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.kob.botrunningsystem.service.BotRunningService;
import com.kob.botrunningsystem.service.impl.utils.Bot;
import com.kob.botrunningsystem.service.impl.utils.BotQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    private final BotQueue botQueue;

    @Autowired
    public BotRunningServiceImpl(BotQueue botQueue) {
        this.botQueue = botQueue;
    }

    @Override
    public JSONObject addBot(Integer userId, String botCode, String gameInfo) {
        // 输出调试信息
        System.out.println("Add bot created by user " + userId);
        botQueue.putBot(new Bot(userId, botCode, gameInfo));
        JSONObject returnInfo = new JSONObject();
        returnInfo.put("status_message", "Success");
        return returnInfo;
    }
}
