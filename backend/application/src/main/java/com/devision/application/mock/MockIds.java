package com.devision.application.mock;

public final class MockIds {

    private MockIds() {}

    // Applicants (from other service)
    public static final String APPLICANT_1 = "applicant-001";
    public static final String APPLICANT_2 = "applicant-002";

    // Companies
    public static final String COMPANY_1 = "company-001";
    public static final String COMPANY_2 = "company-002";

    // Job posts
    public static final String JOB_1 = "job-001"; // belongs to COMPANY_1
    public static final String JOB_2 = "job-002"; // belongs to COMPANY_1
    public static final String JOB_3 = "job-003"; // belongs to COMPANY_2;
}
