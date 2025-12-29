
import { request } from "../../../utils/HttpUtil.js";
import {API_BASE} from "../../../config/api.js";
export const authService = {
    login(payload) {

        return request("/auth/login", { method: "POST", body: payload });
    },

    register(payload) {

        return request("/auth/register", { method: "POST", body: payload });
    },

    googleLogin() {
        // redirect to backend oauth2 entrypoint
        window.location.href = `${API_BASE}/oauth2/authorization/google`;
    },
};
