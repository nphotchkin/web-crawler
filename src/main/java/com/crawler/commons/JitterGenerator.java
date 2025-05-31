package com.crawler.commons;

import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor
public class JitterGenerator {

    public void waitAndJitter() {
        var jitter = (long) (new Random().nextDouble() * 100) + 500;
        try {
            Thread.sleep(jitter);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
