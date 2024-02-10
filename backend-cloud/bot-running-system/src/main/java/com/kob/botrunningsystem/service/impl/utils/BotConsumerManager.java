package com.kob.botrunningsystem.service.impl.utils;

import com.kob.botrunningsystem.utils.BotInterface;
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

@Component
public class BotConsumerManager {
    private static RestTemplate restTemplate;
    private final ThreadPoolTaskExecutor executor;
    private final ThreadPoolTaskScheduler scheduler;
    private final BotQueue botQueue;
    private final List<BotTaskFuture> futureList = Collections.synchronizedList(new LinkedList<>());
    private static final String receiveBotMoveUrl = "http://localhost:3000/pk/receive/bot/move";

    private record BotTaskFuture(Future<?> future, long startTime) {
    }

    private record BotTask(Bot bot) implements Runnable {
        @Override
        public void run() {
            // 获取botCode的运行结果并输出调试信息
            System.out.println("Start to run bot created by user: " + bot.getUserId());
            Integer result = executeBotCode(bot);
            System.out.println("End to run bot created by user: " + bot.getUserId() + " result = " + result);
            // 将结果发送给backend
            MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
            data.add("user_id", bot.getUserId().toString());
            data.add("direction", result.toString());
            restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
        }

        // 编译运行botCode
        private Integer executeBotCode(Bot bot) {
            UUID uuid = UUID.randomUUID();
            String uid = uuid.toString().substring(0, 8);
            BotInterface botInterface = Reflect.compile(
                    "com.kob.botrunningsystem.utils.BotInterfaceImpl" + uid,
                    addUid(bot.getBotCode(), uid)
            ).create().get();
            System.out.println("compile success");
            return botInterface.nextStep(bot.getGameInfo());
        }

        // 在code中Bot类名后面加上uid
        private String addUid(String code, String uid) {
            int index = code.indexOf(" implements com.kob.botrunningsystem.utils.BotInterface");
            return code.substring(0, index) + uid + code.substring(index);
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
                            System.out.println(Thread.currentThread().getName() + " do not need to stop task" + future);
                            iterator.remove();
                            continue;
                        }

                        long currentTime = System.currentTimeMillis();
                        if (currentTime - botTaskFuture.startTime >= 5000) {
                            System.out.println(Thread.currentThread().getName() + " stop task " + future);
                            botTaskFuture.future.cancel(true);
                            iterator.remove();
                        }
                    }
                }
            }, trigger);

            // 从botQueue中读取bot并添加任务
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println(Thread.currentThread().getName() + " try to take bot");
                Bot bot = botQueue.takeBot();
                System.out.println(Thread.currentThread().getName() + " take bot created by user: " + bot.getUserId() + " success");
                Future<?> future = executor.submit(new BotTask(bot));
                futureList.add(new BotTaskFuture(future, System.currentTimeMillis()));
                System.out.println(Thread.currentThread().getName() + " add botTask created by user: " + bot.getUserId() + " success");
            }
        });
        addBotTaskThread.start();
    }
}
