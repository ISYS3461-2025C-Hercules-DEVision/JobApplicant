package com.devision.applicant.kafka.kafka_consumer;

import com.devision.applicant.connection.JmToApplicantCodeWithUuid;
import com.devision.applicant.dto.ProfileUpdateResponseEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class PendingJmRequest {
    private final Map<String, CompletableFuture<ProfileUpdateResponseEvent>> pending = new ConcurrentHashMap<>();

    public CompletableFuture<ProfileUpdateResponseEvent> create(String correlationId){
        CompletableFuture<ProfileUpdateResponseEvent> future = new CompletableFuture<>();
        pending.put(correlationId, future);
        return future;
    }

    public void complete(String correlationId, ProfileUpdateResponseEvent response){
        CompletableFuture<ProfileUpdateResponseEvent> future = pending.remove(correlationId);
        if(future != null){
            future.complete(response);
        }
    }

    public void fail(String correlationId, Exception ex){
        CompletableFuture<ProfileUpdateResponseEvent> future = pending.remove(correlationId);
        if(future != null){
            future.completeExceptionally(ex);
        }
    }

    public ProfileUpdateResponseEvent waitForResponse(String correlationId, long timeout, TimeUnit unit)
            throws Exception {
        CompletableFuture<ProfileUpdateResponseEvent> future = pending.get(correlationId);
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
