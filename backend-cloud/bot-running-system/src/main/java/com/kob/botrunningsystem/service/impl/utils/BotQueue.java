package com.kob.botrunningsystem.service.impl.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BotQueue {
    private final BlockingQueue<Bot> botQueue = new LinkedBlockingQueue<>();

    public void putBot(Bot bot) {
        try {
            botQueue.put(bot);
            System.out.println("Thread " + Thread.currentThread().getName() + " put bot success");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Bot takeBot() {
        try {
            return botQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
