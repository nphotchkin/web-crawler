package com.crawler.infrastructure.adapters.job;

import java.util.concurrent.*;

public class BasicFixedSizedThreadPoolProvider implements ThreadPoolExecutorProvider {

    private final ThreadPoolExecutor executor;
    private static final int threadPoolSize = 5;
    private static final int queueSize = 10;

    public BasicFixedSizedThreadPoolProvider(ThreadPoolExecutor threadPoolExecutor) {
        this.executor = threadPoolExecutor;
    }

    /**
     * Default thread pool having a fixed size backed by a blocking FIFO queue,
     * if the queue overflows then the task will wait until the queue has additional capacity.
     */
    public static BasicFixedSizedThreadPoolProvider withDefaultConfiguration() {

        var executor = new ThreadPoolExecutor(
                threadPoolSize,
                threadPoolSize, // max = pool size to make fixed
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queueSize, true),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        return new BasicFixedSizedThreadPoolProvider(executor);
    }

    @Override
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return executor;
    }

    @Override
    public int threadPoolSize() {
        return threadPoolSize;
    }

    @Override
    public int queueSize() {
        return queueSize;
    }

}
