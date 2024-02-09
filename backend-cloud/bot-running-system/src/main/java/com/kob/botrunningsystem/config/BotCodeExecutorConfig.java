package com.kob.botrunningsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BotCodeExecutorConfig {
    /**
     * 创建并配置一个线程池任务执行器
     *
     * @return 线程池任务执行器
     */
    @Bean(name = "botCodeExecutor")
    public ThreadPoolTaskExecutor botCodeExecutor() {
        // 创建一个线程池任务执行器
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程池大小
        executor.setCorePoolSize(5);
        // 设置最大线程池大小
        executor.setMaxPoolSize(10);
        // 设置队列容量
        executor.setQueueCapacity(25);
        // 初始化线程池
        executor.initialize();
        // 返回线程池任务执行器
        return executor;
    }
}

