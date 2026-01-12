import {request} from "../../../utils/HttpUtil.js";
import { API_BASE_JOB_MANAGER } from "../../../config/api.js";

// ---- Job Applicant API ----

/* =========================
 *  Applicant-service (Gateway)
 * ========================= */

export async function getAllApplicants() {
  return request("/api/v1/applicants", { method: "GET", auth: "admin" });
}

export async function activateApplicant(id) {
  return request(`/api/v1/applicants/${id}/activate`, { method: "PATCH", auth: "admin" });
}

export async function deactivateApplicant(id) {
  return request(`/api/v1/applicants/${id}/deactivate`, { method: "PATCH", auth: "admin" });
}

/* =========================
 *  Application-service (Gateway)
 * ========================= */

export async function getAllApplications() {
  return request("/api/v1/applications", { method: "GET", auth: "admin" });
}

export async function deleteApplication(applicationId) {
  return request(`/api/v1/applications/${applicationId}`, {
    method: "DELETE",
    auth: "admin",
  });
}


// ---- Job Manager API integration ----

async function parseBody(res) {
  const text = await res.text();
  try {
    return text ? JSON.parse(text) : null;
  } catch {
    return text || null;
  }
}

function getAdminToken() {
  // keep multiple keys just in case your auth stores different names
  return (
    localStorage.getItem("admin_access_token") ||
    sessionStorage.getItem("admin_access_token") ||
    localStorage.getItem("admin_token") ||
    sessionStorage.getItem("admin_token")
  );
}

async function jmRequest(path, { method = "GET", body, headers } = {}) {
  const base = API_BASE_JOB_MANAGER.replace(/\/$/, "");
  const url = `${base}${path}`;

  const token = getAdminToken();
  const isFormData = body instanceof FormData;

  const res = await fetch(url, {
    method,
    headers: {
      ...(body && !isFormData ? { "Content-Type": "application/json" } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(headers || {}),
    },
    body: body ? (isFormData ? body : JSON.stringify(body)) : undefined,
  });

  const data = await parseBody(res);

  if (!res.ok) {
    const msg =
      (data && (data.message || data.error || data.detail)) ||
      `JM request failed (${res.status})`;
    const err = new Error(msg);
    err.status = res.status;
    err.data = data;
    throw err;
  }

  return data;
}

export async function getAllJobsFromJM({ page = 1, size = 200 } = {}) {
  const params = new URLSearchParams();
  params.set("page", String(Math.max(1, page)));
  params.set("size", String(size));
  
  return jmRequest(`/internal/jobs/jobs?${params.toString()}`, { method: "GET" });
}

export async function getAllCompaniesFromJM({ page = 1, size = 10 } = {}) {
  const params = new URLSearchParams();
  params.set("page", String(Math.max(1, page)));
  params.set("size", String(size));

  return jmRequest(`/internal/companies/companies?${params.toString()}`, {
    method: "GET",
  });
}
