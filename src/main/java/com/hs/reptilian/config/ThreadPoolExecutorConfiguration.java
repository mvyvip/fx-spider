package com.hs.reptilian.config;


import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolExecutorConfiguration {

    // 线程池维护线程的最少数量
    private int corePoolSize = 50;
    // 线程池维护线程的最大数量
    private int maxPoolSize = 150;
    // 缓存队列
    private int queueCapacity = 500;

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new CallerRunsPolicy());
        return threadPoolTaskExecutor;
    }

    public class RecommitRejected implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                Thread.sleep(RandomUtils.nextInt(10, 200));
                executor.execute(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
