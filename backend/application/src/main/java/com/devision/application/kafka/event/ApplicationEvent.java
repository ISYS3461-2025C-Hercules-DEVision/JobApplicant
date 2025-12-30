package com.devision.application.kafka.event;

import java.time.Instant;

public record ApplicationEvent(
        ApplicationEventType type,
        String applicationId,
        String applicantId,
        String jobPostId,
        String companyId,
        Instant occurredAt
) {}
