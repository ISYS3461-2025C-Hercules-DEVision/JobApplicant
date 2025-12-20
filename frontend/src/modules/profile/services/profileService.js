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

    //DELETE profile
    deleteProfile(applicantId){
        return request(`/api/v1/applicants/${applicantId}`, {method: "DELETE"});
    },
};