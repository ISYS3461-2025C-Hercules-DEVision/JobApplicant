package com.devision.application.connection;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
@Component
public class PendingCompanyRequest<ApplicationCompanyCodeWithUuid> {
    private final Map<String, CompletableFuture<ApplicationCompanyCodeWithUuid>> pending =
            new ConcurrentHashMap<>();

    public CompletableFuture<ApplicationCompanyCodeWithUuid> create(String correlationId) {
        CompletableFuture<ApplicationCompanyCodeWithUuid> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    public void complete(String correlationId, ApplicationCompanyCodeWithUuid payload) {
        CompletableFuture<ApplicationCompanyCodeWithUuid> future = pending.remove(correlationId);
        if (future != null) {
            future.complete(payload);
        }
    }
    public void remove(String correlationId) {
        pending.remove(correlationId);
    }
}
