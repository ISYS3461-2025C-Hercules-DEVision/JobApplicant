package com.devision.authentication.kafka.kafka_consumer;

import com.devision.authentication.connection.AutheticationApplicantCodeWithUuid;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component

public class PendingApplicantRequests<AuthenticationApplicantCodeWithUuid> {

    private final Map<String, CompletableFuture<AuthenticationApplicantCodeWithUuid>> pending =
            new ConcurrentHashMap<>();

    public CompletableFuture<AuthenticationApplicantCodeWithUuid> create(String correlationId) {
        CompletableFuture<AuthenticationApplicantCodeWithUuid> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    public void complete(String correlationId, AuthenticationApplicantCodeWithUuid payload) {
        CompletableFuture<AuthenticationApplicantCodeWithUuid> future = pending.remove(correlationId);
        if (future != null) {
            future.complete(payload);
        }
    }

    public void remove(String correlationId) {
        pending.remove(correlationId);
    }
}

