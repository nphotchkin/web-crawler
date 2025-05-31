package com.crawler.infrastructure.adapters.job.handler;

public interface JobRequestHandler<T> {

    /**
     * Perform work defined by this request handler.
     */
    T perform();

    /**
     * Describe the data that this task is going to operate on when perform is invoked.
     * @return - pretty printable description
     */
    String describeTask();

}
