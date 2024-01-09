package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class GameUtil extends Thread {
    private final GameMapUtil gameMap;
    @Getter
    private final Player player1;
    @Getter
    private final Player player2;
    private Integer player1NextStep = null;
    private Integer player2NextStep = null;
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private String status = "playing";  // playing:游戏正在进行，finished:游戏结束
    private String loser = "";    // all:平局，player1:player1输，player2:player2输

    public GameUtil(Integer size, Integer innerWallsCount, Integer player1Id, Integer player2Id) {
        gameMap = new GameMapUtil(size, innerWallsCount);
        player1 = new Player(player1Id, size - 2, 1, new ArrayList<>());
        player2 = new Player(player2Id, 1, size - 2, new ArrayList<>());
    }

    public void createMap() {
        gameMap.createGameMap();
    }

    public int[][] getMap() {
        return gameMap.getGameMapArray();
    }

    public void setPlayer1NextStep(Integer player1NextStep) {
        reentrantLock.lock();
        try {
            this.player1NextStep = player1NextStep;
        } finally {
            reentrantLock.unlock();
        }
    }

    public void setPlayer2NextStep(Integer player2NextStep) {
        reentrantLock.lock();
        try {
            this.player2NextStep = player2NextStep;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            if (nextStep()) {   // 是否获取了两条蛇的下一步操作
                judge();

                if ("playing".equals(status)) {
                    sendMove();
                } else {
                    sendResult();
                    break;
                }
            } else {    // 如果没有获取到两条蛇的输入，则游戏结束，没有输入的玩家判输
                status = "finished";
                reentrantLock.lock();
                try {
                    if (player1NextStep == null && player2NextStep == null) {
                        loser = "all";
                    } else if (player1NextStep == null) {
                        loser = "player1";
                    } else {
                        loser = "player2";
                    }
                } finally {
                    reentrantLock.unlock();
                }
                sendResult();
                break;
            }
        }
    }

    private boolean checkSnakeAlive(List<Cell> checkSnake, List<Cell> anotherSnake) {
        Cell head = checkSnake.getLast();

        // 检查蛇头是否撞墙
        if (gameMap.getGameMapArray()[head.x][head.y] == 1)
            return false;

        // 检查蛇头是否撞到自己或另一条蛇，如果撞到返回false，没撞到返回true
        return !checkSnake.subList(0, checkSnake.size() - 1).contains(head) && !anotherSnake.contains(head);
    }

    // 判断两名玩家下一步操作是否合法
    private void judge() {
        List<Cell> snake1 = player1.getCells();
        List<Cell> snake2 = player2.getCells();

        boolean isSnake1Alive = checkSnakeAlive(snake1, snake2);
        boolean isSnake2Alive = checkSnakeAlive(snake2, snake1);
        if (!isSnake1Alive || !isSnake2Alive) {
            status = "finished";
            if (!isSnake1Alive && !isSnake2Alive)
                loser = "all";
            else if (!isSnake1Alive)
                loser = "player1";
            else
                loser = "player2";
        }

    }

    // 广播信息的辅助函数
    private void sendMessage(String message) {
        WebSocketServer.users.get(player1.getId()).sendMessage(message);
        WebSocketServer.users.get(player2.getId()).sendMessage(message);
    }

    // 向两个Client发送移动信息
    private void sendMove() {
        // 由于此处需要读入两名玩家的nextstep，所以需要加锁
        reentrantLock.lock();
        try {
            JSONObject response = new JSONObject();
            response.put("event", "move");
            response.put("player1_direction", player1NextStep);
            response.put("player2_direction", player2NextStep);
            player1NextStep = player2NextStep = null;
            sendMessage(response.toJSONString());
        } finally {
            reentrantLock.unlock();
        }
    }

    // 向两个Client公布结果
    private void sendResult() {
        JSONObject response = new JSONObject();
        response.put("event", "result");
        response.put("loser", loser);
        sendMessage(response.toJSONString());
    }

    // 等待两个玩家的下一步操作
    private boolean nextStep() {
        // 进行下一步操作时先等200ms，以防后端速度过快而前端来不及显示
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 5秒内判断5次
        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(100);
                reentrantLock.lock();
                try {
                    // 如果两名玩家的下一步输入都读到了
                    if (player1NextStep != null && player2NextStep != null) {
                        player1.getSteps().add(player1NextStep);
                        player2.getSteps().add(player2NextStep);
                        return true;
                    }
                } finally {
                    reentrantLock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
