package com.juzi.searchhub.configuration;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.juzi.searchhub.model.vo.Picture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Guava Retryer配置
 *
 * @author codejuzi
 */
@Configuration
public class RetryerConfig {

    private static final int MAX_ATTEMPTS = 5;

    private static final long INIT_SLEEP_TIME = 500L;

    private static final long INCR_TIME = 200L;

    @Bean
    public Retryer<String> retryer() {
        // 定义重试器
        return RetryerBuilder.<String>newBuilder()
                // 如果结果为空则重试
                .retryIfResult(Objects::isNull)
                // 发生IO异常则重试
                .retryIfExceptionOfType(IOException.class)
                // 发生运行时异常则重试
                .retryIfRuntimeException()
                // 等待
                .withWaitStrategy(WaitStrategies.incrementingWait(INIT_SLEEP_TIME, TimeUnit.MILLISECONDS, INCR_TIME, TimeUnit.MILLISECONDS))
                // 允许执行5次（首次执行 + 最多重试4次）
                .withStopStrategy(StopStrategies.stopAfterAttempt(MAX_ATTEMPTS))
                .build();
    }

    @Bean
    public Retryer<Page<Picture>> pictureRetryer() {
        return RetryerBuilder.<Page<Picture>>newBuilder()
                .retryIfExceptionOfType(IOException.class)
                .retryIfRuntimeException()
                .withStopStrategy(StopStrategies.stopAfterAttempt(MAX_ATTEMPTS))
                .withWaitStrategy(WaitStrategies.incrementingWait(INIT_SLEEP_TIME, TimeUnit.MILLISECONDS, INCR_TIME, TimeUnit.MILLISECONDS))
                .build();
    }


}