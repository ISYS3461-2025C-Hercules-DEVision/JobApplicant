import { request } from "../../../../utils/HttpUtil.js";

const TOKEN_KEY = "admin_access_token";

export async function adminLoginService(email, password, remember = true) {
    const data = await request("/auth/admin/login", {
        method: "POST",
        body: { email, password },
        auth: "admin", // tell HttpUtil to use admin token slot
    });

    const token = data?.accessToken || data?.token;

    if (!token) {
        throw new Error("Access token not found in admin login response.");
    }

    const storage = remember ? localStorage : sessionStorage;
    storage.setItem(TOKEN_KEY, token);

    return { ...data, token };
}

export async function adminLogoutService() {
    try {
        await request("/auth/logout", { method: "POST", auth: "admin" });
    } catch {
        // ignore
    }

    localStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem("admin_user");
}

export function getAdminToken() {
    return (
        localStorage.getItem(TOKEN_KEY) ||
        sessionStorage.getItem(TOKEN_KEY)
    );
}
