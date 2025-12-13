package com.devision.notification.repository;

import com.devision.notification.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByApplicantId(String applicantId);

    List<Notification> findByApplicantIdAndIsReadFalse(String applicantId);
}
