// src/modules/profile/services/resumeService.js
import { request } from "../../../utils/HttpUtil";

export const resumeService = {
    getResumeByApplicant(applicantId) {
        return request(`/api/v1/applicants/${applicantId}/resumes`);
    },

    updateResume(applicantId, payload) {
        return request(`/api/v1/applicants/${applicantId}/resumes`, {
            method: "PUT",
            body: payload,
        });
    },

    deleteResume(applicantId) {
        return request(`/api/v1/applicants/${applicantId}/resumes`, {
            method: "DELETE",
        });
    },
};