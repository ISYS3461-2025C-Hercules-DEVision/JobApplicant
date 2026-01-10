// src/services/jobService.js
import { API_BASE_JOB_MANAGER } from "../../../config/api.js";

// src/services/jobService.js


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
    return localStorage.getItem("access_token") || localStorage.getItem("token");
}

/**
 * Request helper for Job Manager service
 */
async function jobRequest(
    path,
    { method = "GET", body, headers, auth = "user" } = {}
) {
    const url = `${API_BASE_JOB_MANAGER}${path}`;
    console.log("Job API URL:", url);

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

/**
 * ✅ Correct endpoint:
 * GET /internal/jobs/jobs?page=1&size=10
 *
 * Supports filters if backend supports them (optional):
 * title, location, employmentType, keyWord
 */
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

    params.set("page", String(page));
    params.set("size", String(size));

    const qs = params.toString();
    const path = `/internal/jobs/jobs?${qs}`;

    return jobRequest(path, { method: "GET" });
}

