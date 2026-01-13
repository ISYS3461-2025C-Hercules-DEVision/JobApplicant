import { request } from "../../../utils/HttpUtil.js";

export async function createApplication(payload) {
    if (!payload) throw new Error("Missing payload");

    return request("/api/v1/applications", {
        method: "POST",
        body: payload,
        auth: "user",
    });
}