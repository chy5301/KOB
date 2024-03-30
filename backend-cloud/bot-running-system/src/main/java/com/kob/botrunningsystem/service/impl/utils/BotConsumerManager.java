package com.kob.botrunningsystem.service.impl.utils;

import com.alibaba.fastjson2.JSONObject;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Supplier;

@Component
public class BotConsumerManager {
    private static RestTemplate restTemplate;
    private final ThreadPoolTaskExecutor executor;
    private final ThreadPoolTaskScheduler scheduler;
    private final BotQueue botQueue;
    private final List<BotTaskFuture> futureList = Collections.synchronizedList(new LinkedList<>());
    private static final String receiveBotMoveUrl = "http://localhost:3000/api/pk/receive/bot/move";

    private record BotTaskFuture(Future<?> future, long startTime) {
    }

    private record BotTask(Bot bot) implements Runnable {
        @Override
        public void run() {
            MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
            // 获取botCode的运行结果并输出调试信息
            try {
                System.out.println("Thread " + Thread.currentThread().getName() + " start to run bot created by user " + bot.getUserId());
                Integer result = executeBotCode(bot);
                System.out.println("Thread " + Thread.currentThread().getName() + " end to run bot created by user " + bot.getUserId() + ", result = " + result);

                // 将运行结果发送给backend
                data.add("status_message", "Success");
                data.add("user_id", bot.getUserId().toString());
                data.add("direction", result.toString());
            } catch (Exception e) {
                // 将异常信息发送给backend
                data.add("status_message", "Exception");
                data.add("user_id", bot.getUserId().toString());
                data.add("exception_class", e.getClass().getName());
                data.add("exception_message", e.getMessage());
                System.out.println("Thread " + Thread.currentThread().getName() + " fail to run bot created by user " + bot.getUserId());
                System.out.println("Exception: " + e.getClass());
                System.out.println("Exception message: " + e.getMessage());
            } finally {
                restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
            }
        }

        // 编译运行botCode
        private Integer executeBotCode(Bot bot) {
            // 解码gameInfo
            JSONObject gameInfo = JSONObject.parseObject(bot.getGameInfo());
            int[][] gameMap = gameInfo.getObject("game_map", int[][].class);
            Integer thisPlayerStartX = gameInfo.getInteger("this_player_start_x");
            Integer thisPlayerStartY = gameInfo.getInteger("this_player_start_y");
            List<Integer> thisPlayerSteps = gameInfo.getJSONArray("this_player_steps").toJavaList(Integer.class);
            Integer anotherPlayerStartX = gameInfo.getInteger("another_player_start_x");
            Integer anotherPlayerStartY = gameInfo.getInteger("another_player_start_y");
            List<Integer> anotherPlayerSteps = gameInfo.getJSONArray("another_player_steps").toJavaList(Integer.class);

            // 编译botCode
            UUID uuid = UUID.randomUUID();
            String uid = uuid.toString().substring(0, 8);
            Supplier<Integer> botCode = Reflect.compile(
                    "com.kob.botrunningsystem.utils.Bot" + uid,
                    addUid(bot.getBotCode(), uid)
            ).create(
                    gameMap,
                    thisPlayerStartX,
                    thisPlayerStartY,
                    thisPlayerSteps,
                    anotherPlayerStartX,
                    anotherPlayerStartY,
                    anotherPlayerSteps
            ).get();

            // 执行botCode
            return botCode.get();
        }

        // 在code中Bot类名后面加上uid
        private String addUid(String code, String uid) {
            StringBuilder result = new StringBuilder(code);
            int index1 = result.indexOf(" implements Supplier<Integer>");
            if (index1 != -1) {
                result.insert(index1, uid);
            }
            int index2 = result.indexOf("public Bot");
            if (index2 != -1) {
                result.insert(index2 + "public Bot".length(), uid);
            }
            return result.toString();
        }
    }

    @Autowired
    public BotConsumerManager(
            @Qualifier("botCodeExecutor") ThreadPoolTaskExecutor executor,
            ThreadPoolTaskScheduler scheduler,
            RestTemplate restTemplate,
            BotQueue botQueue
    ) {
        BotConsumerManager.restTemplate = restTemplate;
        this.executor = executor;
        this.scheduler = scheduler;
        this.botQueue = botQueue;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startConsuming() {
        Thread addBotTaskThread = new Thread(() -> {
            Thread.currentThread().setName("addBotTaskThread");
            // 设置定时任务检查所有线程的执行状态
            PeriodicTrigger trigger = new PeriodicTrigger(Duration.ofSeconds(1));
            scheduler.schedule(() -> {
                synchronized (futureList) {
                    Iterator<BotTaskFuture> iterator = futureList.iterator();
                    while (iterator.hasNext()) {
                        BotTaskFuture botTaskFuture = iterator.next();

                        Future<?> future = botTaskFuture.future;
                        if (future.isDone()) {
                            System.out.println("Thread " + Thread.currentThread().getName() + " do not need to stop task, " + future);
                            iterator.remove();
                            continue;
                        }

                        long currentTime = System.currentTimeMillis();
                        if (currentTime - botTaskFuture.startTime >= 5000) {
                            System.out.println("Thread " + Thread.currentThread().getName() + " stop task " + future);
                            botTaskFuture.future.cancel(true);
                            iterator.remove();
                        }
                    }
                }
            }, trigger);

            // 从botQueue中读取bot并添加任务
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Thread " + Thread.currentThread().getName() + " try to take bot");
                Bot bot = botQueue.takeBot();
                System.out.println("Thread " + Thread.currentThread().getName() + " take bot created by user " + bot.getUserId() + " success");
                Future<?> future = executor.submit(new BotTask(bot));
                futureList.add(new BotTaskFuture(future, System.currentTimeMillis()));
                System.out.println("Thread " + Thread.currentThread().getName() + " add botTask created by user " + bot.getUserId() + " success");
            }
        });
        addBotTaskThread.start();
    }
}
