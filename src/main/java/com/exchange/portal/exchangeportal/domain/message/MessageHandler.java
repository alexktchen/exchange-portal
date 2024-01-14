package com.exchange.portal.exchangeportal.domain.message;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface MessageHandler<T> {
    CompletableFuture<Void> handle(List<T> message) throws ExecutionException, InterruptedException;
}
