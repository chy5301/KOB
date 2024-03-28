package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthenticationUtil;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")
public class WebSocketServer {
    // ConcurrentHashMap是一个线程安全的HashMap，用于将用户ID映射到WebSocketServer实例
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;
    private Game game = null;
    private Session session = null;
    private static UserMapper userMapper;
    private static BotMapper botMapper;
    private static RestTemplate restTemplate;

    // 常量
    private static final Integer mapSize;
    private static final Integer innerWallsCount;
    private static final String addPlayerUrl;
    private static final String removePlayerUrl;

    // 静态初始化块
    static {
        mapSize = 16;
        innerWallsCount = 32;
        addPlayerUrl = "http://localhost:3001/player/add";
        removePlayerUrl = "http://localhost:3001/player/remove";
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        System.out.println("Connected!");

        Integer userId = JwtAuthenticationUtil.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }
        System.out.println(users);
    }

    @OnClose
    public void onClose() {
        System.out.println("Disconnected!");
        System.out.println("UserId = " + user.getId());

        if (this.user != null) {
            stopMatching();
            users.remove(this.user.getId());
        }
    }

    // 一般把onMessage当做一个路由，判断一下应该把任务交给谁
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Receive message!");
        System.out.println(message);
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        // 注意：event可能为空，所以不要写event.equals("start-matching")
        if ("start-matching".equals(event)) {
            startMatching(data.getInteger("bot_id"));
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            moveByHuman(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        exception.printStackTrace();
    }

    // 发送信息的工具方法
    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 初始化游戏
    public static void startGame(Integer player1Id, Integer player1BotId, Integer player2Id, Integer player2BotId) {
        User player1 = userMapper.selectById(player1Id);
        User player2 = userMapper.selectById(player2Id);
        Bot bot1 = botMapper.selectById(player1BotId);
        Bot bot2 = botMapper.selectById(player2BotId);

        // 初始化游戏
        Game game = new Game(mapSize, innerWallsCount, player1.getId(), bot1, player2.getId(), bot2);
        game.createMap();
        users.get(player1.getId()).game = game;
        users.get(player2.getId()).game = game;

        // 发送初始化游戏信息给前端
        JSONObject gameInfo = new JSONObject();
        gameInfo.put("player1_id", game.getPlayer1().getId());
        gameInfo.put("player1_startX", game.getPlayer1().getStartX());
        gameInfo.put("player1_startY", game.getPlayer1().getStartY());
        gameInfo.put("player2_id", game.getPlayer2().getId());
        gameInfo.put("player2_startX", game.getPlayer2().getStartX());
        gameInfo.put("player2_startY", game.getPlayer2().getStartY());
        gameInfo.put("game_map", game.getMap());

        // 将游戏信息和匹配成功信息发送给 client1
        JSONObject responseToPlayer1 = new JSONObject();
        responseToPlayer1.put("event", "matching-success");
        responseToPlayer1.put("opponent_username", player2.getUsername());
        responseToPlayer1.put("opponent_photo", player2.getPhoto());
        responseToPlayer1.put("game_info", gameInfo);
        users.get(player1.getId()).sendMessage(responseToPlayer1.toJSONString());

        // 将游戏信息和匹配成功信息发送给 client2
        JSONObject responseToPlayer2 = new JSONObject();
        responseToPlayer2.put("event", "matching-success");
        responseToPlayer2.put("opponent_username", player1.getUsername());
        responseToPlayer2.put("opponent_photo", player1.getPhoto());
        responseToPlayer2.put("game_info", gameInfo);
        users.get(player2.getId()).sendMessage(responseToPlayer2.toJSONString());

        // 等待2s，与web端匹配成功后的等待时间同步
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 启动游戏线程
        game.start();
    }

    // 反馈bot代码执行异常信息
    public void runningException(String exceptionClass, String exceptionMessage) {
        JSONObject exceptionInfo = new JSONObject();
        exceptionInfo.put("event", "bot-running-exception");
        exceptionInfo.put("exception_class", exceptionClass);
        exceptionInfo.put("exception_message", exceptionMessage);
        sendMessage(exceptionInfo.toJSONString());
    }

    // 接收来自bot-running-system的下一步移动
    public void moveByBot(Integer direction) {
        // 判断要移动的是player1还是player2，设置对应玩家的nextStep
        if (game.getPlayer1().getId().equals(user.getId())) {
            game.setPlayer1NextStep(direction);
        } else if (game.getPlayer2().getId().equals(user.getId())) {
            game.setPlayer2NextStep(direction);
        }
    }

    // 接收move消息后设置对应玩家的下一步移动方向
    private void moveByHuman(Integer direction) {
        // 如果当前WebSocket链接的是player1/player2，并且操作模式为人工操作(botId=-1)，设置对应玩家的nextStep
        if (game.getPlayer1().getId().equals(user.getId())) {
            if (game.getPlayer1().getBotId().equals(-1))
                game.setPlayer1NextStep(direction);
        } else if (game.getPlayer2().getId().equals(user.getId())) {
            if (game.getPlayer2().getBotId().equals(-1))
                game.setPlayer2NextStep(direction);
        }
    }

    // 开始匹配
    private void startMatching(Integer botId) {
        System.out.println("Start matching!");
        // 更新user状态
        this.user = userMapper.selectById(this.user.getId());
        // 生成匹配消息
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        data.add("bot_id", botId.toString());
        System.out.println(data);
        // 向matching-system发送addPlayer的post请求
        // RestTemplate.postForObject()的第一个参数是url，第二个参数是发送的data，第三个参数是返回值的类型
        restTemplate.postForObject(addPlayerUrl, data, String.class);
    }

    // 停止匹配
    private void stopMatching() {
        System.out.println("Stop matching!");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        // 向 matching-system 发送 removePlayer 的 post 请求
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }
}
