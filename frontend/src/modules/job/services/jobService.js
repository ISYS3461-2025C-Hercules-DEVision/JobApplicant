import { API_BASE_JOB_MANAGER } from "../../../config/api.js";

async function parseBody(res) {
    const text = await res.text();
    try {
        return text ? JSON.parse(text) : null;
    } catch {
        return text || null;
    }
}

function getAccessToken(auth = "user") {
    if (auth === "admin") {
        return (
            localStorage.getItem("admin_access_token") ||
            sessionStorage.getItem("admin_access_token") ||
            localStorage.getItem("admin_token") ||
            sessionStorage.getItem("admin_token")
        );
    }

    return (
        localStorage.getItem("accessToken") ||     // ✅ add this
        sessionStorage.getItem("accessToken") ||   // ✅ and this
        localStorage.getItem("access_token") ||
        sessionStorage.getItem("access_token") ||
        localStorage.getItem("token") ||
        sessionStorage.getItem("token")
    );
}

async function jobRequest(path, { method = "GET", body, headers, auth = "user" } = {}) {
    const base = API_BASE_JOB_MANAGER.replace(/\/$/, "");
    const url = `${base}${path}`;
    console.log("Job API URL:", url);

    const isFormData = body instanceof FormData;
    const token = getAccessToken(auth);

    console.log("Job API Token exists:", !!token);

    const res = await fetch(url, {
        method,
        headers: {
            ...(body && !isFormData ? { "Content-Type": "application/json" } : {}),
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(headers || {}),
        },
        body: body ? (isFormData ? body : JSON.stringify(body)) : undefined,

        credentials: "omit",
    });

    const data = await parseBody(res);

    if (!res.ok) {
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

export async function getJobs({
                                  title,
                                  location,
                                  employmentType,
                                  keyWord,
                                  page = 1,
                                  size = 10,
                              } = {}) {
    const params = new URLSearchParams();

    if (title) params.set("title", title);
    if (location) params.set("location", location);
    if (employmentType) params.set("employmentType", employmentType);
    if (keyWord) params.set("keyWord", keyWord);

    // ✅ convert UI page (1-based) → backend page index (0-based)
    params.set("page", String(Math.max(0, page - 1)));
    params.set("size", String(size));

    return jobRequest(`/internal/jobs/jobs?${params.toString()}`, { method: "GET" });
}
