package com.crawler.infrastructure.adapters.job;

import com.crawler.application.exception.WebCrawlerException;
import com.crawler.infrastructure.adapters.job.handler.JobRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class JobExecutor {

    private final ThreadPoolExecutorProvider threadPoolExecutorProvider;

    public JobExecutor(ThreadPoolExecutorProvider threadPoolExecutorProvider) {
        this.threadPoolExecutorProvider = threadPoolExecutorProvider;
    }

    public <T> Job<T> execute(JobRequestHandler<T> jobRequestHandler) {
        var job = new Job<>(jobRequestHandler);
        try {
            threadPoolExecutorProvider.getThreadPoolExecutor().submit(() -> {
                try {
                    T result = job.getJobRequestHandler().perform();
                    job.complete(result);
                } catch (Exception e) {
                    // Ignored - because its up to the caller to decide how to deal with a given error.
                    log.warn(
                            "Continuable error: Failed to execute job: '{}, reason: '{}''",
                            jobRequestHandler.describeTask(),
                            e.getMessage(),
                            e
                    );
                    job.failed(e);
                }
            });
        } catch (RejectedExecutionException e) {
            throw WebCrawlerException.shouldNeverHappen(
                    "Failed to schedule job: '%s', reason: '%s'".formatted(
                            jobRequestHandler.describeTask(), e.getMessage()
                    ),
                    e
            );
        }
        return job;
    }

}
