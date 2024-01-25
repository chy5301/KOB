package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    @Override
    public String addPlayer(Integer userId, Integer rating) {
        // 临时调试信息
        System.out.println("Add player: " + userId + " " + rating);
        return "Add player success";
    }

    @Override
    public String removePlayer(Integer userId) {
        // 临时调试信息
        System.out.println("Remove player: " + userId);
        return "Remove player success";
    }
}
