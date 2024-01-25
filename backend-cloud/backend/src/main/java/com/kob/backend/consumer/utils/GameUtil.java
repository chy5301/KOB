package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;
import lombok.Getter;

import java.util.Date;
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
    private final ReentrantLock lock = new ReentrantLock();
    private String status = "playing";  // playing:游戏正在进行，finished:游戏结束
    private String loser = "";    // all:平局，player1:player1输，player2:player2输

    public GameUtil(Integer size, Integer innerWallsCount, Integer player1Id, Integer player2Id) {
        gameMap = new GameMapUtil(size, innerWallsCount);
        player1 = new Player(player1Id, size - 2, 1);
        player2 = new Player(player2Id, 1, size - 2);
    }

    public void createMap() {
        gameMap.createGameMap();
    }

    public int[][] getMap() {
        return gameMap.getGameMapArray();
    }

    public void setPlayer1NextStep(Integer player1NextStep) {
        lock.lock();
        try {
            this.player1NextStep = player1NextStep;
        } finally {
            lock.unlock();
        }
    }

    public void setPlayer2NextStep(Integer player2NextStep) {
        lock.lock();
        try {
            this.player2NextStep = player2NextStep;
        } finally {
            lock.unlock();
        }
    }

    // 启动游戏线程
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
                lock.lock();
                try {
                    if (player1NextStep == null && player2NextStep == null) {
                        loser = "all";
                    } else if (player1NextStep == null) {
                        loser = "player1";
                    } else {
                        loser = "player2";
                    }
                } finally {
                    lock.unlock();
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
        lock.lock();
        try {
            JSONObject response = new JSONObject();
            response.put("event", "move");
            response.put("player1_direction", player1NextStep);
            response.put("player2_direction", player2NextStep);
            player1NextStep = player2NextStep = null;
            sendMessage(response.toJSONString());
        } finally {
            lock.unlock();
        }
    }

    // 将对局记录存储到数据库
    private void saveGameRecord() {
        Record gameRecord = new Record(
                null,
                player1.getId(),
                player1.getStartX(),
                player1.getStartY(),
                player2.getId(),
                player2.getStartX(),
                player2.getStartY(),
                // 将 ArrayList<Integer> 转化为 String
                JSON.toJSONString(player1.getSteps()),
                JSON.toJSONString(player2.getSteps()),
                // 将 int[][] 转化为 String
                JSON.toJSONString(gameMap.getGameMapArray()),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(gameRecord);
    }

    // 向两个Client公布结果
    private void sendResult() {
        JSONObject response = new JSONObject();
        response.put("event", "result");
        response.put("loser", loser);
        saveGameRecord();
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
                lock.lock();
                try {
                    // 如果两名玩家的下一步输入都读到了
                    if (player1NextStep != null && player2NextStep != null) {
                        player1.nextStep(player1NextStep);
                        player2.nextStep(player2NextStep);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
