package com.devision.applicant.connection;

import java.math.BigDecimal;

public record SubscriptionApplicantCodeWithUuid(String correlationId, String applicantId, BigDecimal minSalary, BigDecimal maxSalary) {
}
