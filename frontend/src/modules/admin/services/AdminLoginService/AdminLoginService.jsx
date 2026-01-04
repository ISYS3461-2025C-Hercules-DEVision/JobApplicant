import { request } from "../../../../utils/HttpUtil.js";

const TOKEN_KEY = "admin_token";


export async function adminLoginService(email, password, remember = true) {
    //  change this endpoint if needed
    const data = await request("/auth/admin/login", {
        method: "POST",
        body: { email, password },
    });
    console.log("Admin login response:", data);
    // support different backend formats
    const token = data?.token || data?.accessToken;


    if (!token) {
        throw new Error("Token not found in login response.");
    }

    const storage = remember ? localStorage : sessionStorage;
    storage.setItem(TOKEN_KEY, token);

    return { ...data, token};
}

export function adminLogoutService() {
    localStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(TOKEN_KEY);

}

export function getAdminToken() {
    return localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY);
}


