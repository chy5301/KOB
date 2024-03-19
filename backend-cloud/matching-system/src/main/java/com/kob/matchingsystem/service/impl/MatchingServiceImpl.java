package com.kob.matchingsystem.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    public static MatchingPool matchingPool;

    @Autowired
    public void setMatchingPool(MatchingPool matchingPool) {
        MatchingServiceImpl.matchingPool = matchingPool;
    }

    @Override
    public JSONObject addPlayer(Integer userId, Integer rating, Integer botId) {
        // 临时调试信息
        System.out.println("Add player: " + userId + " " + rating);
        matchingPool.addPlayer(userId, rating, botId);
        JSONObject returnInfo = new JSONObject();
        returnInfo.put("status_message", "Success");
        return returnInfo;
    }

    @Override
    public JSONObject removePlayer(Integer userId) {
        // 临时调试信息
        System.out.println("Remove player: " + userId);
        matchingPool.removePlayer(userId);
        JSONObject returnInfo = new JSONObject();
        returnInfo.put("status_message", "Success");
        return returnInfo;
    }
}
