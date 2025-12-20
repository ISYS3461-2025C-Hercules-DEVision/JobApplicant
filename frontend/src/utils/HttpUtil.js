// src/services/http.js
export const API_BASE = "http://localhost:10789";

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

    const isFormData = body instanceof FormData;
    const res = await fetch(`${API_BASE}${path}`, {
        method,
        headers: {
            ...(body && !isFormData ? { "Content-Type": "application/json" } : {}),
            ...(headers || {}),
        },
        body: body ? (isFormData ? body : JSON.stringify(body)) : undefined,
        credentials: "include", //  cookies/session for oauth
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
