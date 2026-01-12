
import { request } from "../../../utils/HttpUtil";

export const resumeService = {
    getResumeByApplicant(applicantId) {
        return request(`/api/v1/applicants/${applicantId}/resumes`, {method : "GET"});
    },

    updateResume(applicantId, payload) {
        return request(`/api/v1/applicants/${applicantId}/resumes`, {
            method: "PUT",
            body: payload,
        });
    },

    deleteResume(applicantId, fieldName) {
        return request(`/api/v1/applicants//${applicantId}/field/${fieldName}`, {
            method: "DELETE",
        });
    },

};