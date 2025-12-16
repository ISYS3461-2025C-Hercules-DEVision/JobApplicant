package com.devision.authentication.kafka.kafka_consumer;

import com.devision.authentication.connection.AutheticationApplicantCodeWithUuid;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component

public class PendingApplicantRequests {

    private final Map<String, CompletableFuture<AutheticationApplicantCodeWithUuid>> pending =
            new ConcurrentHashMap<>();

    public CompletableFuture<AutheticationApplicantCodeWithUuid> create(String correlationId) {
        CompletableFuture<AutheticationApplicantCodeWithUuid> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    public void complete(String correlationId, AutheticationApplicantCodeWithUuid payload) {
        CompletableFuture<AutheticationApplicantCodeWithUuid> future = pending.remove(correlationId);
        if (future != null) {
            future.complete(payload);
        }
    }

    public void remove(String correlationId) {
        pending.remove(correlationId);
    }
}

