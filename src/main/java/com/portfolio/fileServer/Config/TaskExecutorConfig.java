package com.portfolio.fileServer.Config;

import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TaskExecutorConfig implements WebMvcConfigurer {
    
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        long timeOut = -1;
        configurer.setDefaultTimeout(timeOut);
        configurer.setTaskExecutor(taskExcutor());
    }

    @Bean
    public AsyncTaskExecutor taskExcutor() {
        ThreadPoolTaskExecutorBuilder executorBuilder = new ThreadPoolTaskExecutorBuilder();
        ThreadPoolTaskExecutor executor = executorBuilder.corePoolSize(10).maxPoolSize(50).queueCapacity(100).build();
        executor.initialize();
        return executor;
    }
}
