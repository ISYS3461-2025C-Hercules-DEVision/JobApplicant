package com.devision.applicant.kafka.kafka_consumer;

import com.devision.applicant.connection.ApplicantToJmCodeWithUuid;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class PendingJmRequest {
    private final Map<String, CompletableFuture<ApplicantToJmCodeWithUuid>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<ApplicantToJmCodeWithUuid> create(String correlationId){
        CompletableFuture<ApplicantToJmCodeWithUuid> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    public void complete(String correlationId, ApplicantToJmCodeWithUuid response){
        CompletableFuture<ApplicantToJmCodeWithUuid> future = pending.remove(correlationId);
        if(future != null){
            future.complete(response);
        }
    }

    public void fail(String correlationId, Exception ex){
        CompletableFuture<ApplicantToJmCodeWithUuid> future = pending.remove(correlationId);
        if(future != null){
            future.completeExceptionally(ex);
        }
    }

    public ApplicantToJmCodeWithUuid waitForResponse(String correlationId, long timeout, TimeUnit unit)
            throws Exception {
        CompletableFuture<ApplicantToJmCodeWithUuid> future = pending.get(correlationId);
        if (future == null) {
            throw new IllegalStateException("No pending update for correlationID: " + correlationId);
        }
        try {
            return future.get(timeout, unit);
        } catch (TimeoutException e) {
            pending.remove(correlationId);
            throw new Exception("JM did not response in time", e);
        }
    }
}
