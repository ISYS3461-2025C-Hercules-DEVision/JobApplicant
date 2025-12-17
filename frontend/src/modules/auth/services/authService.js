
import { request, API_BASE } from "../../../utils/HttpUtil.js";

export const authService = {
    login(payload) {
        // payload: { email, password }
        return request("/auth/login", { method: "POST", body: payload });
    },

    register(payload) {
        // payload should match your backend DTO
        return request("/auth/register", { method: "POST", body: payload });
    },

    googleLogin() {
        // redirect to backend oauth2 entrypoint
        window.location.href = `${API_BASE}/oauth2/authorization/google`;
    },
};
