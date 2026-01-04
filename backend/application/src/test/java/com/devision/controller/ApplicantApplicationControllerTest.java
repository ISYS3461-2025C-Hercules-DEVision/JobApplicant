package com.devision.application.controller;

import com.devision.application.api.internal.ApplicationService;
import com.devision.application.dto.external.request.CreateApplicationRequest;
import com.devision.application.dto.internal.command.CreateApplicationCommand;
import com.devision.application.testutil.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ApplicantApplicationController.class)
class ApplicantApplicationControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean ApplicationService applicationService;

    @Test
    @WithMockUser(username = "applicant-1", roles = {"APPLICANT"})
    void apply_shouldCallServiceAndReturn200() throws Exception {
        var view = TestDataFactory.applicationView("applicant-1", "job-1", "company-1");
        when(applicationService.create(any(CreateApplicationCommand.class))).thenReturn(view);

        CreateApplicationRequest req = new CreateApplicationRequest();
        req.setJobPostId("job-1");
        req.setCompanyId("company-1");

        mvc.perform(post("/api/v1/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicantId").value("applicant-1"))
                .andExpect(jsonPath("$.jobPostId").value("job-1"))
                .andExpect(jsonPath("$.companyId").value("company-1"));

        ArgumentCaptor<CreateApplicationCommand> captor = ArgumentCaptor.forClass(CreateApplicationCommand.class);
        verify(applicationService).create(captor.capture());
        assertThat(captor.getValue().applicantId()).isEqualTo("applicant-1");
        assertThat(captor.getValue().jobPostId()).isEqualTo("job-1");
        assertThat(captor.getValue().companyId()).isEqualTo("company-1");
    }

    @Test
    @WithMockUser(username = "applicant-1", roles = {"APPLICANT"})
    void listMyApplications_shouldReturnList() throws Exception {
        when(applicationService.listByApplicant("applicant-1"))
                .thenReturn(List.of(
                        TestDataFactory.applicationSummaryView("job-1", "company-1"),
                        TestDataFactory.applicationSummaryView("job-2", "company-2")
                ));

        mvc.perform(get("/api/v1/applications/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(applicationService).listByApplicant("applicant-1");
    }

    @Test
    @WithMockUser(username = "applicant-1", roles = {"APPLICANT"})
    void getMyApplication_shouldCheckOwnershipViaService() throws Exception {
        var view = TestDataFactory.applicationView("applicant-1", "job-1", "company-1");
        when(applicationService.getOwnedByApplicant("applicant-1", "app-123")).thenReturn(view);

        mvc.perform(get("/api/v1/applications/me/app-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicantId").value("applicant-1"));

        verify(applicationService).getOwnedByApplicant("applicant-1", "app-123");
    }

    @Test
    @WithMockUser(username = "applicant-1", roles = {"APPLICANT"})
    void uploadCv_shouldCallService() throws Exception {
        var view = TestDataFactory.applicationView("applicant-1", "job-1", "company-1");
        when(applicationService.uploadCv(any())).thenReturn(view);

        MockMultipartFile file = new MockMultipartFile(
                "file", "cv.pdf", "application/pdf", "fake".getBytes()
        );

        mvc.perform(multipart("/api/v1/applications/me/app-123/cv")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(applicationService).uploadCv(any());
    }

    @Test
    @WithMockUser(username = "applicant-1", roles = {"APPLICANT"})
    void uploadCoverLetter_shouldCallService() throws Exception {
        var view = TestDataFactory.applicationView("applicant-1", "job-1", "company-1");
        when(applicationService.uploadCoverLetter(any())).thenReturn(view);

        MockMultipartFile file = new MockMultipartFile(
                "file", "cover.pdf", "application/pdf", "fake".getBytes()
        );

        mvc.perform(multipart("/api/v1/applications/me/app-123/cover-letter")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        verify(applicationService).uploadCoverLetter(any());
    }
}
