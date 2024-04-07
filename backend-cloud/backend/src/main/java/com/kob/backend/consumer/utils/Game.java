package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    private final GameMap gameMap;
    @Getter
    private final Player player1;
    @Getter
    private final Player player2;
    private Integer player1NextStep = null;
    private Integer player2NextStep = null;
    private final ReentrantLock lock = new ReentrantLock();
    private String status = "playing";  // playing:游戏正在进行，finished:游戏结束
    private String loser = "";    // all:平局，player1:player1输，player2:player2输
    private static final String addBotUrl = "http://kob-bot-running-system:3003/api/bot/task";

    public Game(Integer size, Integer innerWallsCount, Integer player1Id, Bot bot1, Integer player2Id, Bot bot2) {
        gameMap = new GameMap(size, innerWallsCount);

        Integer bot1Id = -1, bot2Id = -1;
        String bot1Code = "", bot2Code = "";
        if (bot1 != null) {
            bot1Id = bot1.getId();
            bot1Code = bot1.getContent();
        }
        if (bot2 != null) {
            bot2Id = bot2.getId();
            bot2Code = bot2.getContent();
        }

        player1 = new Player(player1Id, bot1Id, bot1Code, size - 2, 1);
        player2 = new Player(player2Id, bot2Id, bot2Code, 1, size - 2);
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
                    endGame();
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
                endGame();
                break;
            }
        }
    }

    // 检查玩家是否存活
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

    // 获取游戏当前状态信息
    private String encodeGetGameInfo(Player player) {
        Player thisPlayer, anotherPlayer;
        if (player1.getId().equals(player.getId())) {
            thisPlayer = player1;
            anotherPlayer = player2;
        } else {
            thisPlayer = player2;
            anotherPlayer = player1;
        }

        JSONObject gameInfo = new JSONObject();
        gameInfo.put("game_map", getMap());
        gameInfo.put("this_player_start_x", thisPlayer.getStartX());
        gameInfo.put("this_player_start_y", thisPlayer.getStartY());
        gameInfo.put("this_player_steps", thisPlayer.getSteps());
        gameInfo.put("another_player_start_x", anotherPlayer.getStartX());
        gameInfo.put("another_player_start_y", anotherPlayer.getStartY());
        gameInfo.put("another_player_steps", anotherPlayer.getSteps());
        return gameInfo.toJSONString();
    }

    // 发送bot代码给bot-running-system执行
    private void sendBotCodeIfValidId(Player player) {
        // botId为-1代表人工操作
        if (player.getBotId().equals(-1))
            return;

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("game_info", encodeGetGameInfo(player));
        CommonBeanProvider.restTemplate.postForObject(addBotUrl, data, String.class);
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

    // 更新两名玩家的天梯分
    private void updateUserRating() {
        User user1 = CommonBeanProvider.userMapper.selectById(player1.getId());
        User user2 = CommonBeanProvider.userMapper.selectById(player2.getId());

        if ("player1".equals(loser)) {
            user1.setRating(user1.getRating() - 2);
            user2.setRating(user2.getRating() + 5);
        } else if ("player2".equals(loser)) {
            user1.setRating(user1.getRating() + 5);
            user2.setRating(user2.getRating() - 2);
        }
        CommonBeanProvider.userMapper.updateById(user1);
        CommonBeanProvider.userMapper.updateById(user2);
        System.out.println("Update user" + user1.getId() + " rating: " + user1.getRating() + " user" + user2.getId() + " rating: " + user2.getRating());
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
        CommonBeanProvider.recordMapper.insert(gameRecord);
    }

    // 向两个Client公布游戏结果
    private void sendResult() {
        JSONObject response = new JSONObject();
        response.put("event", "result");
        response.put("loser", loser);
        sendMessage(response.toJSONString());
    }

    // 结束游戏
    private void endGame() {
        // 更新两名玩家的天梯分
        updateUserRating();
        // 将对局记录存储到数据库
        saveGameRecord();
        // 向两个Client发送游戏结果
        sendResult();
    }

    // 等待两个玩家的下一步操作
    private boolean nextStep() {
        // 进行下一步操作时先等200ms，以防后端速度过快而前端来不及显示
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 判断是否使用Bot，将Bot代码发送给bot-running-system处理
        sendBotCodeIfValidId(player1);
        sendBotCodeIfValidId(player2);

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
