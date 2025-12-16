package com.devision.notification.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.devision.notification.enums.NotificationType;


import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {

    @Id
    private String notificationId;   // UUID

    private String applicantId;      // string reference only, no FK

    private NotificationType type;            // "ApplicationUpdate" | "JobMatch" | "System"
    private String message;

    private String jobPostId;       // from JM
    private String companyId;       // from JM

    private List<String> skillMatches;   // array<string>

    private boolean isRead;
    private Instant createdAt;      // timestamp
}
