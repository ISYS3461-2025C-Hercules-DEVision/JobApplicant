package com.devision.subscription.service;

import com.devision.subscription.dto.JobPostEventDTO;
import com.devision.subscription.model.Notification;

import java.util.List;

public interface NotificationService {
    void evaluateAndNotify(JobPostEventDTO event);

    List<Notification> listForApplicant(String applicantId);
}
