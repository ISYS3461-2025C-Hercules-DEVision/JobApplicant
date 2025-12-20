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


}