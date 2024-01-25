package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    public static MatchingPoolUtil matchingPool;

    @Autowired
    public void setMatchingPool(MatchingPoolUtil matchingPool) {
        MatchingServiceImpl.matchingPool = matchingPool;
        // 启动匹配线程
        MatchingServiceImpl.matchingPool.start();
    }

    @Override
    public String addPlayer(Integer userId, Integer rating) {
        // 临时调试信息
        System.out.println("Add player: " + userId + " " + rating);
        matchingPool.addPlayer(userId, rating);
        return "Add player success";
    }

    @Override
    public String removePlayer(Integer userId) {
        // 临时调试信息
        System.out.println("Remove player: " + userId);
        matchingPool.removePlayer(userId);
        return "Remove player success";
    }
}
