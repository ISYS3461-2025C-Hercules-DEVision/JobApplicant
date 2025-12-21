import {request} from "../../../utils/HttpUtil.js";

export const profileService = {

    //GET applicant profile by ID
    getProfile(id){
        return request(`/api/v1/applicants/${id}`, {method: "GET"});
    },

    //UPDATE applicant profile by ID
    updateProfile(id, updateData){
        return request(`/api/v1/applicants/${id}`, {
            method: "PUT",
            body: updateData,
        });
    },

    //UPLOAD avatar
    uploadAvatar(applicantId, file){
        const formData = new FormData();
        formData.append("file", file);

        return request(`/api/v1/applicants/${applicantId}/avatar`, {
            method : "POST",
            body : formData,
            headers : {}, //browser sets multipart / form-data
        });
    },

    //DELETE profile by specific field
    deleteProfileByField(applicantId, fieldName){
        return request(`/api/v1/applicants/${applicantId}/fieldName/${fieldName}`,{
            method: "DELETE",
        });
    },

    //UPLOAD portfolio (either Image or Video)
    uploadMediaPortfolio(applicantId, file, title = "", description = "", visibility = "PRIVATE"){
        const formData = new FormData();
        formData.append("file", file);
        formData.append("title", title);
        formData.append("description", description);
        formData.append("visibility", visibility);

        return request(`/api/v1/applicants/${applicantId}/portfolio`, {
            method: "POST",
            body: formData,
            headers: {},
        });
    },

    //GET portfolio
    getMediaPortfolio(applicantId, visibility = null){
        const params = visibility ? `?visibility=${visibility}` : '';
        return request(`/api/v1/applicants/${applicantId}/portfolio${params}`,{
            method : "GET"
        });
    },
};