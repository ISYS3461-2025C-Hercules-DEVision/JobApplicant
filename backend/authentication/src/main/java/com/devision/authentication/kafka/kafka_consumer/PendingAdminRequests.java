package com.devision.authentication.kafka.kafka_consumer;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class PendingAdminRequests <AuthenticationAdminCodeWithUuid> {
    private final Map<String, CompletableFuture<AuthenticationAdminCodeWithUuid>> pending =
            new ConcurrentHashMap<>();

    public CompletableFuture<AuthenticationAdminCodeWithUuid> create(String correlationId) {
        CompletableFuture<AuthenticationAdminCodeWithUuid> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    public void complete(String correlationId, AuthenticationAdminCodeWithUuid payload) {
        CompletableFuture<AuthenticationAdminCodeWithUuid> future = pending.remove(correlationId);
        if (future != null) {
            future.complete(payload);
        }
    }

    public void remove(String correlationId) {
        pending.remove(correlationId);
    }
}
