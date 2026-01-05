import {request} from "../../../utils/HttpUtil.js";

export async function getAllApplicants(){
    return request("/api/v1/applicants", {
        method: "GET",
    });
}
export async function activateApplicant(id) {
    return request(`/api/v1/applicants/${id}/activate`, {
        method: "PATCH",
    });
}

export async function deactivateApplicant(id) {
    return request(`/api/v1/applicants/${id}/deactivate`, {
        method: "PATCH",
    });
}