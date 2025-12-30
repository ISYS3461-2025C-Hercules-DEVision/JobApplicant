@WebMvcTest(controllers = CompanyApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class CompanyApiControllerTest {

    @Autowired MockMvc mvc;

    @MockBean ApplicationService applicationService;

    @Test
    void listByCompany_returns200() throws Exception {
        var v = new ApplicationSummaryView();
        v.applicationId = "app-002";
        v.jobPostId = "job-002";
        v.companyId = "company-001";
        v.status = ApplicationStatus.SUBMITTED;
        v.createdAt = Instant.parse("2025-01-02T00:00:00Z");

        when(applicationService.listByCompany("company-001"))
                .thenReturn(List.of(v));

        // Nếu endpoint là /api/v1/company/applications/by-company/{companyId}
        mvc.perform(get("/api/v1/company/applications/by-company/company-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].applicationId").value("app-002"));
    }
}
