package com.kob.backend.consumer;

import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")
public class WebSocketServer {

    // ConcurrentHashMap是一个线程安全的哈希表，用于将用户ID映射到WebSocketServer实例
    private static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;
    private Session session = null;
    private static UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        this.session = session;
        System.out.println("Connected!");
        System.out.println(session.toString());

        Integer userId = Integer.parseInt(token);
        this.user = userMapper.selectById(userId);
        users.put(userId, this);
    }

    @OnClose
    public void onClose() {
        System.out.println("Disconnected!");
        System.out.println(this.session.toString());

        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Receive message!");
        System.out.println(message);
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
}
