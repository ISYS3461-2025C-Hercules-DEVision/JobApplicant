package com.devision.application.api.internal;

import com.devision.application.dto.internal.command.CreateApplicationCommand;
import com.devision.application.dto.internal.command.UploadCoverLetterCommand;
import com.devision.application.dto.internal.command.UploadCvCommand;
import com.devision.application.dto.internal.view.ApplicationSummaryView;
import com.devision.application.dto.internal.view.ApplicationView;

import java.util.List;

public interface ApplicationService {

    ApplicationView create(CreateApplicationCommand cmd);

    List<ApplicationSummaryView> listByApplicant(String applicantId);

    ApplicationView getOwnedByApplicant(String applicantId, String applicationId);

    ApplicationView uploadCv(UploadCvCommand cmd);

    ApplicationView uploadCoverLetter(UploadCoverLetterCommand cmd);

    List<ApplicationSummaryView> listByJobPost(String jobPostId);

    List<ApplicationSummaryView> listByCompany(String companyId);

    ApplicationView getById(String applicationId);

    List<ApplicationView> listAll();
}
