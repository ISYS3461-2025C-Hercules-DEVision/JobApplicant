package com.devision.notification.repository;

import com.devision.notification.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.devision.notification.enums.NotificationType;


import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByApplicantId(String applicantId);

    List<Notification> findByApplicantIdAndIsReadFalse(String applicantId);

    List<Notification> findByApplicantIdAndType(String applicantId, NotificationType type
    );
}
