package com.devision.authentication.kafka.kafka_consumer;

import com.devision.authentication.connection.AuthenticationApplicantForAdminCodeWithUuid;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PendingApplicantForAdminRequests {
    private final Map<String, CompletableFuture<AuthenticationApplicantForAdminCodeWithUuid>> pending =
            new ConcurrentHashMap<>();

    public CompletableFuture<AuthenticationApplicantForAdminCodeWithUuid> create(String correlationId) {
        CompletableFuture<AuthenticationApplicantForAdminCodeWithUuid> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    public void complete(String correlationId, AuthenticationApplicantForAdminCodeWithUuid payload) {
        CompletableFuture<AuthenticationApplicantForAdminCodeWithUuid> future = pending.remove(correlationId);
        if (future != null) {
            future.complete(payload);
        }
    }

    public void remove(String correlationId) {
        pending.remove(correlationId);
    }
}
