package com.crawler.infrastructure.adapters.job;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class BasicFixedSizedThreadPoolProviderTest {

    @Test
    void threadPoolSize_should_beBeSameAsConfiguredForCorePoolSize() {
        var threadPoolProvider = BasicFixedSizedThreadPoolProvider.withDefaultConfiguration();
        assertEquals(threadPoolProvider.threadPoolSize(), threadPoolProvider.getThreadPoolExecutor().getCorePoolSize());
    }

    @Test
    void threadPoolSize_should_beSameAsConfiguredForMaximumPoolSize() {
        var threadPoolProvider = BasicFixedSizedThreadPoolProvider.withDefaultConfiguration();
        assertEquals(threadPoolProvider.threadPoolSize(), threadPoolProvider.getThreadPoolExecutor().getMaximumPoolSize());
    }

    @Test
    void queueCapacity_should_beSameAsConfigured() {
        var threadPoolProvider = BasicFixedSizedThreadPoolProvider.withDefaultConfiguration();
        var queue = threadPoolProvider.getThreadPoolExecutor().getQueue();
        assertInstanceOf(ArrayBlockingQueue.class, queue);

        var capacity = ((ArrayBlockingQueue<?>) queue).remainingCapacity() + queue.size();
        assertEquals(threadPoolProvider.queueSize(), capacity);
    }

}