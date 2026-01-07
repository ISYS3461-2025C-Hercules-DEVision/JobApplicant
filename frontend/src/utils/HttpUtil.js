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
            sessionStorage.getItem("admin_token")  // legacy fallback
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
 * Refresh access token using HttpOnly refreshToken cookie.
 * This will be used for BOTH user and admin sessions (same cookie).
 *
 * If you want separate admin refresh response later, we can add /auth/admin/refresh.
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

    //  store token in the correct place (user/admin)
    // For admin, default remember=true because refresh happens silently anyway
    setAccessToken(data.accessToken, auth, true);

    //  update user info (only if user auth)
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
 * request(path, { auth: "user" | "admin", remember: boolean })
 *
 * - auth: which token to attach
 * - remember: only used for admin token storage when setting token manually (login)
 */
export async function request(
    path,
    { method = "GET", body, headers, retry = true, auth = "user" } = {}
) {
    const url = `${API_BASE}${path}`;
    console.log(`Actual url being called: ${url}`);

    const isFormData = body instanceof FormData;
    const token = getAccessToken(auth);

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

    // Auto refresh when access token expired
    if (res.status === 401 && retry && path !== "/auth/refresh") {
        try {
            const newToken = await refreshAccessToken(auth);

            return request(path, {
                method,
                body,
                headers: {
                    ...(headers || {}),
                    Authorization: `Bearer ${newToken}`,
                },
                retry: false,
                auth,
            });
        } catch (refreshErr) {
            clearAccessToken(auth);
            throw refreshErr;
        }
    }

    const data = await parseBody(res);

    if (!res.ok) {
        // banned check
        if (res.status === 403) {
            // if user banned
            clearAccessToken(auth);

            // You can redirect differently for admin if you want:
            if (auth === "admin") {
                window.location.href = "/adminLogin";
            } else {
                window.location.href = "/BannedAccount";
            }
        }

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