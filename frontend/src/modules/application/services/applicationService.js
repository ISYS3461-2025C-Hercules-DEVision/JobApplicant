import { request } from "../../../utils/HttpUtil.js";

/**
 * If your controller has @RequestMapping("/applications")
 * then endpoint is: /applications/applicant/{applicantId}
 *
 * If NOT, then endpoint is: /applicant/{applicantId}
 *
 * ✅ Choose ONE and delete the other, based on your backend.
 */

// ✅ OPTION 1 (most common): /applications/applicant/{id}
export async function getApplicationsByApplicantId(applicantId) {
    if (!applicantId) throw new Error("applicantId is required");

    return request(`/applications/applicant/${encodeURIComponent(applicantId)}`, {
        method: "GET",
        auth: "user",
    });
}

// ✅ OPTION 2 (if your controller has no base path)
// export async function getApplicationsByApplicantId(applicantId) {
//   if (!applicantId) throw new Error("applicantId is required");
//
//   return request(`/applicant/${encodeURIComponent(applicantId)}`, {
//     method: "GET",
//     auth: "user",
//   });
// }

export async function getApplicationById(applicationId) {
    if (!applicationId) throw new Error("applicationId is required");

    return request(`/applications/${encodeURIComponent(applicationId)}`, {
        method: "GET",
        auth: "user",
    });
}
