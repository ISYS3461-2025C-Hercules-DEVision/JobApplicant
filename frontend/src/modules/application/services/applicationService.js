import { request } from "../../../utils/HttpUtil.js";


//  /applications/applicant/{id}
export async function getApplicationsByApplicantId(applicantId) {
    if (!applicantId) throw new Error("applicantId is required");

    return request(`/api/v1/applications/applicant/${encodeURIComponent(applicantId)}`, {
        method: "GET",
        auth: "user",
    });
}

