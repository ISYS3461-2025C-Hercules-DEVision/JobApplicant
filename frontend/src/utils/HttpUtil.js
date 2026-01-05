import { API_BASE } from "../config/api.js";

async function parseBody(res) {
    const text = await res.text();
    try {
        return text ? JSON.parse(text) : null;
    } catch {
        return text || null;
    }
}

function getAccessToken() {
    // new key + fallback to old key
    return localStorage.getItem("access_token");
}

function setAccessToken(token) {
    if (!token) return;
    localStorage.setItem("access_token", token);
    localStorage.removeItem("token"); // legacy cleanup
}

function clearAccessToken() {
    localStorage.removeItem("access_token");
    localStorage.removeItem("token");
    localStorage.removeItem("user");
}

async function refreshAccessToken() {
    const url = `${API_BASE}/auth/refresh`;

    const res = await fetch(url, {
        method: "POST",
        credentials: "include",
    });

    const data = await parseBody(res);

    if (!res.ok) {
        const msg =
            (data && (data.message || data.error || data.detail)) ||
            `Refresh failed (${res.status})`;
        throw new Error(msg);
    }

    if (!data?.accessToken) {
        throw new Error("Refresh response missing accessToken");
    }

    setAccessToken(data.accessToken);

    // Optionally update user too (recommended)
    if (data.userId) {
        const user = {
            userId: data.userId,
            applicantId: data.applicantId,
            email: data.email,
            fullName: data.fullName,
            status: data.status,
        };
        localStorage.setItem("user", JSON.stringify(user));
    }

    return data.accessToken;
}

export async function request(path, { method = "GET", body, headers, retry = true } = {}) {
    const url = `${API_BASE}${path}`;
    console.log(`Actual url being called: ${url}`);

    const isFormData = body instanceof FormData;
    const token = getAccessToken();

    const res = await fetch(url, {
        method,
        headers: {
            ...(body && !isFormData ? { "Content-Type": "application/json" } : {}),
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(headers || {}),
        },
        body: body ? (isFormData ? body : JSON.stringify(body)) : undefined,
        credentials: "include", // cookie refresh token
    });

    // Auto refresh when token expired
    if (res.status === 401 && retry && path !== "/auth/refresh") {
        try {
            const newToken = await refreshAccessToken();

            return request(path, {
                method,
                body,
                headers: {
                    ...(headers || {}),
                    Authorization: `Bearer ${newToken}`,
                },
                retry: false,
            });
        } catch (refreshErr) {
            clearAccessToken();
            throw refreshErr;
        }
    }

    const data = await parseBody(res);

    if (!res.ok) {
        // banned check
        if (res.status === 403) {
            clearAccessToken();
            window.location.href = "/BannedAccount";
        }

        const message =
            (data && (data.message || data.error || data.detail)) ||
            `Request failed (${res.status})`;

        // Preserve status in the thrown error (helpful later)
        const err = new Error(message);
        err.status = res.status;
        err.data = data;
        throw err;
    }

    return data;
}
