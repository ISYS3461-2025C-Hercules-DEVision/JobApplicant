import { API_BASE } from "../config/api.js";

async function parseBody(res) {
    const text = await res.text();
    try {
        return text ? JSON.parse(text) : null;
    } catch {
        return text || null;
    }
}

export async function request(path, { method = "GET", body, headers } = {}) {
    const url = `${API_BASE}${path}`;
    console.log(`Actual url being called: ${url}`);

    //Boolean check for updating Image or Video
    const isFormData = body instanceof FormData;

    //  Use user token first, fallback to admin token
    const token =
        localStorage.getItem("token") ||
        localStorage.getItem("admin_token") ||
        sessionStorage.getItem("admin_token");

    const res = await fetch(url, {
        method,
        headers: {
            ...(body && !isFormData ? { "Content-Type": "application/json" } : {}),
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(headers || {}),
        },
        body: body ? (isFormData ? body : JSON.stringify(body)) : undefined,
        credentials: "include",
    });

    const data = await parseBody(res);

    if (!res.ok) {
        const message =
            (data && (data.message || data.error || data.detail)) ||
            `Request failed (${res.status})`;
        throw new Error(message);
    }

    return data;
}
