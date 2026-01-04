package com.devision.application.testutil;

import com.devision.application.dto.internal.view.ApplicationSummaryView;
import com.devision.application.dto.internal.view.ApplicationView;
import com.devision.application.enums.ApplicationStatus;
import com.devision.application.enums.FileType;

import java.time.Instant;
import java.util.UUID;

public final class TestDataFactory {

    private TestDataFactory() {}

    public static ApplicationView applicationView(String applicantId, String jobPostId, String companyId) {
        ApplicationView v = new ApplicationView();
        v.applicationId = UUID.randomUUID().toString();
        v.applicantId = applicantId;
        v.jobPostId = jobPostId;
        v.companyId = companyId;
        v.status = ApplicationStatus.SUBMITTED.name();
        v.createdAt = Instant.now();
        v.updatedAt = Instant.now();

        v.applicantCV = fileView("cv");
        v.coverLetter = fileView("cover-letter");
        return v;
    }

    public static ApplicationSummaryView applicationSummaryView(String jobPostId, String companyId) {
        ApplicationSummaryView v = new ApplicationSummaryView();
        v.applicationId = UUID.randomUUID().toString();
        v.jobPostId = jobPostId;
        v.companyId = companyId;
        v.status = ApplicationStatus.SUBMITTED.name();
        v.createdAt = Instant.now();
        return v;
    }

    public static ApplicationView.FileView fileView(String prefix) {
        ApplicationView.FileView f = new ApplicationView.FileView();
        f.fileId = prefix + "-" + UUID.randomUUID();
        f.fileUrl = "https://cdn.example.com/" + prefix + ".pdf";
        f.fileType = FileType.PDF;
        f.createdAt = Instant.now();
        return f;
    }
}
