import { API_BASE } from "../config/api.js";

async function parseBody(res) {
    const text = await res.text();
    try {
        return text ? JSON.parse(text) : null;
    } catch {
        return text || null;
    }
}

/**
 * auth = "user" | "admin"
 */
function getAccessToken(auth = "user") {
    if (auth === "admin") {
        return (
            localStorage.getItem("admin_access_token") ||
            sessionStorage.getItem("admin_access_token") ||
            localStorage.getItem("admin_token") || // legacy fallback
            sessionStorage.getItem("admin_token") // legacy fallback
        );
    }

    // user token
    return (
        localStorage.getItem("access_token") ||
        localStorage.getItem("token") // legacy fallback
    );
}

function setAccessToken(token, auth = "user", remember = true) {
    if (!token) return;

    if (auth === "admin") {
        const storage = remember ? localStorage : sessionStorage;
        storage.setItem("admin_access_token", token);

        // legacy cleanup
        localStorage.removeItem("admin_token");
        sessionStorage.removeItem("admin_token");
        return;
    }

    // user token
    localStorage.setItem("access_token", token);
    localStorage.removeItem("token"); // legacy cleanup
}

function clearAccessToken(auth = "user") {
    if (auth === "admin") {
        localStorage.removeItem("admin_access_token");
        sessionStorage.removeItem("admin_access_token");

        // legacy cleanup
        localStorage.removeItem("admin_token");
        sessionStorage.removeItem("admin_token");
        localStorage.removeItem("admin_user");
        return;
    }

    localStorage.removeItem("access_token");
    localStorage.removeItem("token");
    localStorage.removeItem("user");
}

/**
 * Prevent attaching Authorization to auth endpoints.
 */
function isAuthRoute(path = "") {
    return path.startsWith("/auth/");
}

function stripAuthHeader(headers = {}) {
    const h = { ...(headers || {}) };
    delete h.Authorization;
    delete h.authorization;
    return h;
}

/**
 * Refresh access token using HttpOnly refreshToken cookie.
 * This will be used for BOTH user and admin sessions (same cookie).
 */
async function refreshAccessToken(auth = "user") {
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

    // store token in the correct place (user/admin)
    setAccessToken(data.accessToken, auth, true);

    // update user info (only if user auth)
    if (auth === "user" && data.userId) {
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

/**
 * request(path, { auth: "user" | "admin" })
 *
 * OPTIONS:
 * - credentials: "include" | "omit" | "same-origin"
 *   default is "include"
 */
export async function request(
    path,
    {
        method = "GET",
        body,
        headers,
        retry = true,
        auth = "user",
        credentials = "include",
    } = {}
) {
    const url = `${API_BASE}${path}`;

    const isFormData = body instanceof FormData;
    const authRoute = isAuthRoute(path);

    //  Only attach token for non-auth routes
    const token = authRoute ? null : getAccessToken(auth);

    //  For auth routes, force-remove Authorization even if caller provided it
    const safeHeaders = authRoute ? stripAuthHeader(headers) : headers;

    //  Build headers explicitly so we can log them
    const finalHeaders = {
        ...(body && !isFormData ? { "Content-Type": "application/json" } : {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...(safeHeaders || {}),
    };

    //  Show exactly what is being sent
    console.log(" OUTGOING REQUEST", {
        url,
        path,
        method,
        credentials,
        headers: finalHeaders,
        body,
        rawBody: body
            ? isFormData
                ? "[FormData]"
                : JSON.stringify(body)
            : null,
    });

    const res = await fetch(url, {
        method,
        headers: finalHeaders,
        body: body ? (isFormData ? body : JSON.stringify(body)) : undefined,
        credentials,
    });

    // ✅ Log raw response status + headers
    console.log(" RESPONSE", {
        url,
        status: res.status,
        statusText: res.statusText,
        responseHeaders: Object.fromEntries(res.headers.entries()),
    });

    // Auto refresh when access token expired
    if (res.status === 401 && retry && path !== "/auth/refresh") {
        try {
            const newToken = await refreshAccessToken(auth);

            const retryHeaders = isAuthRoute(path)
                ? stripAuthHeader(headers)
                : {
                    ...(headers || {}),
                    Authorization: `Bearer ${newToken}`,
                };

            return request(path, {
                method,
                body,
                headers: retryHeaders,
                retry: false,
                auth,
                credentials,
            });
        } catch (refreshErr) {
            clearAccessToken(auth);
            throw refreshErr;
        }
    }

    const data = await parseBody(res);

    if (!res.ok) {
        console.log(" API ERROR BODY", {
            url,
            status: res.status,
            data,
        });

        const message =
            (data && (data.message || data.error || data.detail)) ||
            `Request failed (${res.status})`;

        const err = new Error(message);
        err.status = res.status;
        err.data = data;
        throw err;
    }

    return data;
}

