package com.devision.application.controller;

import com.devision.application.api.internal.ApplicationService;
import com.devision.application.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CompanyApiController.class)
class CompanyApiControllerTest {

    @Autowired MockMvc mvc;

    @MockBean ApplicationService applicationService;

    @Test
    @WithMockUser(username = "company-1", roles = {"COMPANY"})
    void listByJobPost_shouldReturnList() throws Exception {
        when(applicationService.listByJobPost("job-1"))
                .thenReturn(List.of(
                        TestDataFactory.applicationSummaryView("job-1", "company-1")
                ));

        mvc.perform(get("/api/v1/partner/applications/by-job/job-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(applicationService).listByJobPost("job-1");
    }

    @Test
    @WithMockUser(username = "company-1", roles = {"COMPANY"})
    void listByCompany_shouldReturnList() throws Exception {
        when(applicationService.listByCompany("company-1"))
                .thenReturn(List.of(
                        TestDataFactory.applicationSummaryView("job-1", "company-1")
                ));

        mvc.perform(get("/api/v1/partner/applications/by-company/company-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(applicationService).listByCompany("company-1");
    }

    @Test
    @WithMockUser(username = "company-1", roles = {"COMPANY"})
    void getById_shouldReturnApplication() throws Exception {
        when(applicationService.getById("app-123"))
                .thenReturn(TestDataFactory.applicationView("applicant-1","job-1","company-1"));

        mvc.perform(get("/api/v1/partner/applications/app-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobPostId").value("job-1"));

        verify(applicationService).getById("app-123");
    }
}
