
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

    //UPLOAD portfolio (either Image or Video)
    uploadMediaPortfolio(resumeId, file, title = "", description = "", visibility = "PRIVATE"){
        const formData = new FormData();
        formData.append("file", file);
        formData.append("title", title);
        formData.append("description", description);
        formData.append("visibility", visibility);

        return request(`/api/v1/applicants/${resumeId}/portfolio`, {
            method: "POST",
            body: formData,
            headers: {},
        });
    },

    //GET portfolio
    getMediaPortfolio(resumeId, visibility = null){
        const params = visibility ? `?visibility=${visibility}` : '';
        return request(`/api/v1/applicants/${resumeId}/portfolio${params}`,{
            method : "GET"
        });
    },

    //DELETE portfolio
    deleteMedia(resumeId, mediaId){
        return request(`/api/v1/applicants/${resumeId}/portfolio/${mediaId}`,{
            method : "DELETE"
        });
    },
};