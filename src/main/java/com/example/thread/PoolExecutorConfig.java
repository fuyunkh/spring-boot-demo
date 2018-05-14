package com.example.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author tianyj.
 * @Date 2017/6/7 16:43
 * @description java线程池配置类
 */

@Configuration
@EnableAsync
public class PoolExecutorConfig {

    /**
     * 设置ThreadPoolExecutor核心池size.
     */
    private int corePoolSize = 50;
    /**
     * 设置ThreadPoolExecutor最大池size.
     */
    private int maxPoolSize = 300;
    /**
     * 设置ThreadPoolExecutor的阻塞队列容量
     */
    private int queueCapacity = 20;

    /**
     * @param
     * @return Executor
     * @function 自定义spring线程池配置信息
     */
    @Bean
    public Executor myAsync() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("todo-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
