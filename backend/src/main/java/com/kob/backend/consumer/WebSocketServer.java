package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.GameUtil;
import com.kob.backend.consumer.utils.JwtAuthenticationUtil;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")
public class WebSocketServer {

    // ConcurrentHashMap是一个线程安全的HashMap，用于将用户ID映射到WebSocketServer实例
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    // CopyOnWriteArraySet是一个线程安全Set，作为匹配池
    private static final CopyOnWriteArraySet<User> matchPool = new CopyOnWriteArraySet<>();
    private User user;
    private GameUtil game = null;
    private Session session = null;
    private static UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        System.out.println("Connected!");

        Integer userId = JwtAuthenticationUtil.getUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            users.put(userId, this);
            // System.out.println("UserId = " + user.getId());
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
            users.remove(this.user.getId());
            matchPool.remove(this.user);
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
            startMatching();
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable exception) {
        exception.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMatching() {
        System.out.println("Start matching!");
        matchPool.add(this.user);

        // 临时调试用，后续换成微服务
        while (matchPool.size() >= 2) {
            Iterator<User> iterator = matchPool.iterator();
            User player1 = iterator.next();
            User player2 = iterator.next();
            matchPool.remove(player1);
            matchPool.remove(player2);

            GameUtil game = new GameUtil(16, 32, player1.getId(), player2.getId());
            game.createMap();
            users.get(player1.getId()).game = game;
            users.get(player2.getId()).game = game;

            game.start();

            JSONObject gameInfo = new JSONObject();
            gameInfo.put("player1_id", game.getPlayer1().getId());
            gameInfo.put("player1_startX", game.getPlayer1().getStartX());
            gameInfo.put("player1_startY", game.getPlayer1().getStartY());
            gameInfo.put("player2_id", game.getPlayer2().getId());
            gameInfo.put("player2_startX", game.getPlayer2().getStartX());
            gameInfo.put("player2_startY", game.getPlayer2().getStartY());
            gameInfo.put("game_map", game.getMap());

            JSONObject responseToPlayer1 = new JSONObject();
            responseToPlayer1.put("event", "matching-success");
            responseToPlayer1.put("opponent_username", player2.getUsername());
            responseToPlayer1.put("opponent_photo", player2.getPhoto());
            responseToPlayer1.put("game_info", gameInfo);
            users.get(player1.getId()).sendMessage(responseToPlayer1.toJSONString());

            JSONObject responseToPlayer2 = new JSONObject();
            responseToPlayer2.put("event", "matching-success");
            responseToPlayer2.put("opponent_username", player1.getUsername());
            responseToPlayer2.put("opponent_photo", player1.getPhoto());
            responseToPlayer2.put("game_info", gameInfo);
            users.get(player2.getId()).sendMessage(responseToPlayer2.toJSONString());
        }
    }

    private void stopMatching() {
        System.out.println("Stop matching!");
        matchPool.remove(this.user);
    }

    // 接收move消息后设置对应玩家的下一步移动方向
    private void move(int direction) {
        // 如果当前WebSocket链接的是player1/player2，设置对应玩家的nextStep
        if (game.getPlayer1().getId().equals(user.getId())) {
            game.setPlayer1NextStep(direction);
        } else if (game.getPlayer2().getId().equals(user.getId())) {
            game.setPlayer2NextStep(direction);
        }
    }
}
