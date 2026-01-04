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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ApplicationController.class)
class ApplicationAdminControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean ApplicationService applicationService;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getById_adminShouldWork() throws Exception {
        when(applicationService.getById("app-1"))
                .thenReturn(TestDataFactory.applicationView("applicant-1","job-1","company-1"));

        mvc.perform(get("/api/admin/applications/app-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyId").value("company-1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listByApplicant_adminShouldWork() throws Exception {
        when(applicationService.listByApplicant("applicant-1"))
                .thenReturn(List.of(TestDataFactory.applicationSummaryView("job-1","company-1")));

        mvc.perform(get("/api/admin/applications/by-applicant/applicant-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createForApplicant_adminShouldWork() throws Exception {
        when(applicationService.create(any(CreateApplicationCommand.class)))
                .thenReturn(TestDataFactory.applicationView("applicant-1","job-1","company-1"));

        CreateApplicationRequest req = new CreateApplicationRequest();
        req.setJobPostId("job-1");
        req.setCompanyId("company-1");

        mvc.perform(post("/api/admin/applications?applicantId=applicant-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk());

        ArgumentCaptor<CreateApplicationCommand> captor = ArgumentCaptor.forClass(CreateApplicationCommand.class);
        verify(applicationService).create(captor.capture());
        assertThat(captor.getValue().applicantId()).isEqualTo("applicant-1");
    }

    @Test
    @WithMockUser(username = "someone", roles = {"APPLICANT"})
    void adminEndpoints_nonAdminShould403() throws Exception {
        mvc.perform(get("/api/admin/applications/app-1"))
                .andExpect(status().isForbidden());
    }
}
