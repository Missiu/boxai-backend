package com.boxai.config.threadpool;

import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class ThreadPoolExecutorConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        // 创建线程池工厂
        ThreadFactory threadFactory = new ThreadFactory() {
            // 初始化线程数
            private  int count = 1;
            @Override
            public Thread newThread(@NonNull Runnable r) {
                // 创建一个新的线程，并使用给定的Runnable任务
                Thread thread = new Thread(r);

                // 为线程设置名称，基于count值动态生成，确保每个线程名称唯一
                thread.setName("genChartThread: "+ count);

                // 递增计数器，以便为下一个线程生成唯一的名称
                count++;

                // 返回新创建的线程实例
                return thread;
            }
        };
        // 创建一个新的线程池，核心线程数4，最大线程数6，非核心线程空闲时间100，任务队列为阻塞队列长度为30，自定义线程池工厂
        return new ThreadPoolExecutor(6, 8, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<>(30),threadFactory);
    }
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
