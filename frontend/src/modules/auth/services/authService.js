
import { request } from "../../../utils/HttpUtil.js";
import {API_BASE} from "../../../config/api.js";
export const authService = {
    login(payload) {
        //  Normalize payload to guarantee correct JSON structure
        const cleanPayload = {
            email: payload?.email ?? "",
            password: payload?.password ?? "",
        };
        console.log("LOGIN CALLED", payload, new Date().toISOString());
        return request("/auth/login", { method: "POST", body: cleanPayload });
    },

    register(payload) {
        return request("/auth/register", { method: "POST", body: payload });
    },

    refresh() {
        //  refresh token cookie sent automatically (credentials: include)
        return request("/auth/refresh", { method: "POST" });
    },

    logout() {
        //  backend clears refresh cookie + revokes refresh token
        return request("/auth/logout", { method: "POST" });
    },

    googleLogin() {
        window.location.href = `${API_BASE}/oauth2/authorization/google`;
    },
};
