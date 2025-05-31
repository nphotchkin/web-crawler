package com.crawler.infrastructure.adapters.job;

import com.crawler.infrastructure.adapters.job.handler.JobRequestHandler;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.concurrent.CompletableFuture;

@ToString
@EqualsAndHashCode
public class Job<T> {

    private JobStatus jobStatus;
    private final JobRequestHandler<T> jobRequestHandler;
    private final CompletableFuture<T> result;

    public Job(JobRequestHandler<T> jobRequestHandler) {
        this.jobStatus = JobStatus.NOT_STARTED;
        this.jobRequestHandler = jobRequestHandler;
        this.result = new CompletableFuture<>();
    }

    public enum JobStatus {
        NOT_STARTED, PROCESSING, COMPLETE, FAILED
    }

    public CompletableFuture<T> getResultFuture() {
        return this.result;
    }

    public JobStatus getStatus() {
        return jobStatus;
    }

    protected void failed(Exception e) {
        this.jobStatus = JobStatus.FAILED;
        result.completeExceptionally(e);
    }

    protected void complete(T value) {
        this.jobStatus = JobStatus.COMPLETE;
        result.complete(value);
    }

    protected JobRequestHandler<T> getJobRequestHandler() {
        return this.jobRequestHandler;
    }

}
