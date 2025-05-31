package com.crawler.acceptance;

import com.crawler.infrastructure.adapters.job.BasicFixedSizedThreadPoolProvider;
import com.crawler.infrastructure.adapters.job.JobExecutor;
import com.crawler.infrastructure.adapters.job.ThreadPoolExecutorProvider;
import com.crawler.infrastructure.adapters.job.handler.JobRequestHandler;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class JobExecutorIntegrationTest {

    @Test
    void basicFixedSizedThreadPoolProvider_processAllJobsWithoutFailure() throws InterruptedException {
        // Given - a bunch of tasks queued that go beyond capacity (CallerRunsPolicy triggers)
        var threadPoolProvider = BasicFixedSizedThreadPoolProvider.withDefaultConfiguration();
        var tinyExecutor = new JobExecutor(threadPoolProvider);
        assertThatAllExecuted(tinyExecutor, 100, threadPoolProvider.threadPoolSize(), threadPoolProvider.queueSize());
    }

    @Test // An even more constrained environment verify it doesn't blow up
    void tinyExampleExecutor_should_processAllJobsWithoutFailure() throws InterruptedException {
        // Given - a bunch of tasks queued that go beyond capacity (CallerRunsPolicy triggers)
        var threadPoolProvider = TinyThreadPoolProvider.withDefaultConfiguration();
        var tinyExecutor = new JobExecutor(threadPoolProvider);
        assertThatAllExecuted(tinyExecutor, 100, threadPoolProvider.threadPoolSize(), threadPoolProvider.queueSize());
    }

    private void assertThatAllExecuted(
            JobExecutor jobExecutor,
            int extraTasks,
            int threadCount,
            int queueCapacity
    ) throws InterruptedException {
        int totalTasks = threadCount + queueCapacity + extraTasks;

        var schedulingLatch = new CountDownLatch(totalTasks);
        var completionLatch = new CountDownLatch(totalTasks);
        var handler = new DummyJobRequestHandler();

        // When - all tasks have been queued

        for (int i = 0; i < totalTasks; i++) {
            new Thread(() -> {
                try {
                    var job = jobExecutor.execute(handler);
                    schedulingLatch.countDown(); // mark as scheduled

                    job.getResultFuture().whenComplete((result, throwable) -> {
                        completionLatch.countDown(); // mark as completed
                    });
                } catch (Exception e) {
                    throw e;
                }
            }).start();
        }
        // Then - after reasonable waiting period all should complete without failure

        // Wait for all tasks to be scheduled first
        boolean allScheduled = schedulingLatch.await(5, TimeUnit.SECONDS);
        assertThat(allScheduled).as("All tasks should be scheduled").isTrue();

        boolean allCompleted = completionLatch.await(10, TimeUnit.SECONDS);
        assertThat(allCompleted).as("All tasks should complete within timeout").isTrue();
        assertThat(handler.count.get()).isEqualTo(totalTasks);
    }

    static class DummyJobRequestHandler implements JobRequestHandler<Integer> {

        private final AtomicInteger count = new AtomicInteger(0);

        public DummyJobRequestHandler() {}

        @Override
        public Integer perform() {
            System.out.printf("job request invoked count: '%s'%n", count.incrementAndGet());
            try {
                Thread.sleep(50); // Simulate work.
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return count.get();
        }

        @Override
        public String describeTask() {
            return "Description";
        }
    }

    private static final int THREAD_COUNT = 2;
    private static final int QUEUE_CAPACITY = 5;

    public static class TinyThreadPoolProvider implements ThreadPoolExecutorProvider {

        private final ThreadPoolExecutor executor;

        public TinyThreadPoolProvider(ThreadPoolExecutor threadPoolExecutor) {
            this.executor = threadPoolExecutor;
        }

        /**
         * Default thread pool having a fixed size backed by a blocking FIFO queue,
         * if the queue overflows then the task will wait until the queue has additional capacity.
         */
        public static ThreadPoolExecutorProvider withDefaultConfiguration() {
            var executor = new ThreadPoolExecutor(
                    THREAD_COUNT,
                    THREAD_COUNT,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(QUEUE_CAPACITY, true),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
            return new TinyThreadPoolProvider(executor);
        }

        @Override
        public ThreadPoolExecutor getThreadPoolExecutor() {
            return executor;
        }

        @Override
        public int threadPoolSize() {
            return THREAD_COUNT;
        }

        @Override
        public int queueSize() {
            return QUEUE_CAPACITY;
        }

    }

}
