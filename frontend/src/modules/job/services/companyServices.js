import { jobRequest } from "./jobService.js";

export async function getCompanyPublicProfile(companyId) {
    if (!companyId) throw new Error("companyId is required");

    return jobRequest(`/internal/companies/public-profile/${companyId}`, {
        method: "GET",
    });
}