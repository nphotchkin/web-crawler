package com.crawler.infrastructure.adapters.job;

import java.util.concurrent.ThreadPoolExecutor;

public interface ThreadPoolExecutorProvider {

    ThreadPoolExecutor getThreadPoolExecutor();

    int threadPoolSize();

    int queueSize();

}
