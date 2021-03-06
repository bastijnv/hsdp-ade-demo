package com.philips.microservices.util;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;

import java.util.concurrent.Callable;

public class MDCHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new MDCHystrixContextCallable<>(callable);
    }
}
