import {request} from "../../../utils/HttpUtil.js";

export const searchProfileService = {
    getAll(applicantId){
        return request(`/api/v1/search-profiles/applicant/${applicantId}`)
    },

    create(data) {
        return request("/api/v1/search-profiles", {
            method: "POST",
            body: JSON.stringify(data),
        });
    },

    update(id, data) {
        return request(`/api/v1/search-profiles/${id}`, {
            method: "PUT",
            body:JSON.stringify(data),
        });
    },

    delete(id) {
        return request(`api/v1/search-profiles/${id}`,{
            method: "DELETE"
        });
    },
};