package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool implements Runnable {
    private static List<Player> matchingPool = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;
    private static final String startGameUrl = "http://localhost:3000/pk/start/game";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    // 整个Spring Boot应用启动完成并且所有Spring Beans已经初始化完毕之后自动启动匹配线程
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        Thread matchingPoolThread = new Thread(this);
        matchingPoolThread.start();
    }

    // 添加用户到匹配池
    public void addPlayer(Integer userId, Integer rating, Integer botId) {
        lock.lock();
        try {
            matchingPool.add(new Player(userId, rating, botId, 0));
        } finally {
            lock.unlock();
        }
    }

    // 从匹配池中删除用户
    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            /*
            从matchingPool集合中移除所有userId等于指定userId的元素。
            lambda表达式允许我们直接在集合上进行操作和过滤，而不需要迭代器或者foreach循环
            removeIf方法会对matchingPool集合中的每一个元素应用给定的Predicate，
            如果该元素满足Predicate条件(userId相等)，就将其从集合中移除
            */
            matchingPool.removeIf(player -> player.getUserId().equals(userId));
        } finally {
            lock.unlock();
        }
    }

    // 所有匹配池中的玩家等待时间+1
    private void increaseWaitingTime() {
        for (Player player : matchingPool) {
            player.increaseWaitingTime();
        }
    }

    // 判断两名玩家的天梯分是否匹配
    private boolean checkRatingMatched(Player player1, Player player2) {
        int ratingDiff = Math.abs(player1.getRating() - player2.getRating());
        // 取两名玩家waitingTime的最小值，即要彼此都可以匹配到才匹配成功
        int waitingTime = Math.min(player1.getWaitingTime(), player2.getWaitingTime());
        return ratingDiff <= waitingTime * 10;
    }

    // 尝试匹配matchingPool中的所有玩家
    private void matchPlayers() {
        // 输出调试信息
        System.out.println("Match players: " + matchingPool.toString());
        boolean[] usedPlayers = new boolean[matchingPool.size()];
        for (int i = 0; i < matchingPool.size(); i++) {
            if (usedPlayers[i])
                continue;
            for (int j = i + 1; j < matchingPool.size(); j++) {
                if (usedPlayers[j])
                    continue;
                Player player1 = matchingPool.get(i);
                Player player2 = matchingPool.get(j);

                // 防止自己匹配到自己，遇到相同id的匹配请求保留等待时间更短的请求
                if (player1.getUserId().equals(player2.getUserId())) {
                    usedPlayers[i] = true;
                    continue;
                }

                if (checkRatingMatched(player1, player2)) {
                    usedPlayers[i] = usedPlayers[j] = true;
                    sendResult(player1, player2);
                    break;
                }
            }
        }

        // 删除已被匹配的Player，删除元素较多的情况下重新构造列表的方式效率更高
        List<Player> newPlayerList = new ArrayList<>();
        for (int i = 0; i < matchingPool.size(); i++) {
            if (!usedPlayers[i]) {
                newPlayerList.add(matchingPool.get(i));
            }
        }
        matchingPool = newPlayerList;
    }

    // 向backend发送匹配结果
    private void sendResult(Player player1, Player player2) {
        // 输出调试信息
        System.out.println("Send result: " + player1 + " " + player2);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("player1_id", player1.getUserId().toString());
        data.add("player1_bot_id", player1.getBotId().toString());
        data.add("player2_id", player2.getUserId().toString());
        data.add("player2_bot_id", player2.getBotId().toString());
        // 向 backend 发送 startGame 的 post 请求
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    // 匹配线程
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
