import { API_BASE_URL } from "@/config/api";
import sendHttpRequest from "@/utils/HttpUtil";

const BASE = `${API_BASE_URL}/api/v1/applications`;

export async function apply(jobPostId, companyId) {
  return sendHttpRequest(`${BASE}`, "POST", JSON.stringify({ jobPostId, companyId }));
}

export async function listMyApplications() {
  return sendHttpRequest(`${BASE}/me`, "GET");
}

export async function getMyApplication(applicationId) {
  return sendHttpRequest(`${BASE}/me/${applicationId}`, "GET");
}

export async function uploadCv(applicationId, file) {
  const url = `${BASE}/me/${applicationId}/cv`;
  const form = new FormData();
  form.append("file", file);

  return fetch(url, {
    method: "POST",
    body: form,
    headers: {
      Authorization: localStorage.getItem("token") ? `Bearer ${localStorage.getItem("token")}` : ""
    }
  }).then(async (res) => ({ json: await res.json().catch(() => ({})), status: res.status }));
}

export async function uploadCoverLetter(applicationId, file) {
  const url = `${BASE}/me/${applicationId}/cover-letter`;
  const form = new FormData();
  form.append("file", file);

  return fetch(url, {
    method: "POST",
    body: form,
    headers: {
      Authorization: localStorage.getItem("token") ? `Bearer ${localStorage.getItem("token")}` : ""
    }
  }).then(async (res) => ({ json: await res.json().catch(() => ({})), status: res.status }));
}
