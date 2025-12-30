@WebMvcTest(controllers = ApplicantApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicantApplicationControllerTest {

    @Autowired MockMvc mvc;

    @MockBean ApplicationService applicationService;

    @Test
    void myApplications_returns200() throws Exception {
        var v = new ApplicationSummaryView();
        v.applicationId = "app-001";
        v.jobPostId = "job-001";
        v.companyId = "company-001";
        v.status = ApplicationStatus.SUBMITTED;
        v.createdAt = Instant.parse("2025-01-01T00:00:00Z");

        when(applicationService.listByApplicant(anyString()))
                .thenReturn(List.of(v));

        mvc.perform(get("/api/v1/applications/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].applicationId").value("app-001"))
                .andExpect(jsonPath("$[0].jobPostId").value("job-001"));
    }
}
