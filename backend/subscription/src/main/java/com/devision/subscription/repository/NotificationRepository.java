package com.devision.subscription.repository;

import com.devision.subscription.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByApplicantIdOrderByMatchedAtDesc(String applicantId);
}
