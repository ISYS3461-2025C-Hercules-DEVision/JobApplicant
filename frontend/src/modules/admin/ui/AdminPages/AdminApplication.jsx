import React, { useEffect, useMemo, useState } from "react";
import * as adminService from "../../services/adminService.js";

function formatInstant(v) {
  if (!v) return "-";
  try {
    const d = new Date(v);
    if (Number.isNaN(d.getTime())) return String(v);
    return d.toISOString().slice(0, 10);
  } catch {
    return String(v);
  }
}

function normalizeList(data) {
  // support: array | {data: []} | {content: []}
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.data)) return data.data;
  if (Array.isArray(data?.content)) return data.content;
  return [];
}

export default function AdminApplication() {
  const [q, setQ] = useState("");

  const [applications, setApplications] = useState([]);
  const [applicants, setApplicants] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [companies, setCompanies] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // 1) fetch data
  useEffect(() => {
    let mounted = true;

    (async () => {
      setLoading(true);
      setError(null);

      try {
        const [appsRes, applicantsRes, jobsRes, companiesRes] = await Promise.all([
        adminService.getAllApplications(),
        adminService.getAllApplicants(),
        adminService.getAllJobsFromJM({ page: 0, size: 200 }),
        adminService.getAllCompaniesFromJM({ page: 0, size: 200 }),
        ]);

        if (!mounted) return;

        setApplications(normalizeList(appsRes));
        setApplicants(normalizeList(applicantsRes));
        setJobs(normalizeList(jobsRes));
        setCompanies(normalizeList(companiesRes));
      } catch (err) {
        if (!mounted) return;
        setError(err?.message || "Failed to load admin applications");
      } finally {
        setLoading(false);
      }
    })();

    return () => {
      mounted = false;
    };
  }, []);

  // 2) build maps
  const applicantNameById = useMemo(() => {
    const m = new Map();
    for (const a of applicants) {
      const id = a.applicantId ?? a.id;
      const name = a.fullName ?? a.name;
      if (id) m.set(String(id), name || String(id));
    }
    return m;
  }, [applicants]);

  const jobById = useMemo(() => {
    const m = new Map();
    for (const j of jobs) {
      const id = j.jobId ?? j.id ?? j.jobPostId;
      if (id) m.set(String(id), j);
    }
    return m;
  }, [jobs]);

  const companyById = useMemo(() => {
    const m = new Map();
    for (const c of companies) {
      const id = c.companyId ?? c.id;
      if (id) m.set(String(id), c);
    }
    return m;
  }, [companies]);

  // 3) enrich + search
  const rows = useMemo(() => {
    const s = q.trim().toLowerCase();

    const enriched = applications.map((app) => {
      const applicationId = app.applicationId ?? app.id;
      const applicantId = app.applicantId;
      const jobPostId = app.jobPostId;
      const companyId = app.companyId;

      const applicantName =
        applicantNameById.get(String(applicantId)) || String(applicantId || "-");

      const job = jobById.get(String(jobPostId));
      const jobTitle = job?.title || String(jobPostId || "-");

      // optional: company name (nếu bạn muốn hiển thị sau)
      const company = companyById.get(String(companyId));
      const companyName = company?.companyName || company?.name || company?.company || "";

      const submittedAt = formatInstant(app.submissionDate);
      const status = String(app.status || "");

      return {
        id: String(applicationId),
        applicantName,
        jobTitle,
        companyName,
        submittedAt,
        status,
        _raw: app,
      };
    });

    if (!s) return enriched;

    return enriched.filter((x) => {
      return (
        x.applicantName.toLowerCase().includes(s) ||
        x.jobTitle.toLowerCase().includes(s) ||
        x.status.toLowerCase().includes(s) ||
        x.submittedAt.toLowerCase().includes(s) ||
        (x.companyName || "").toLowerCase().includes(s)
      );
    });
  }, [q, applications, applicantNameById, jobById, companyById]);

  return (
    <div className="card shadow-sm border-0">
      <div className="card-body">
        <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
          <h5 className="mb-0">Applications (CV)</h5>

          <div className="d-flex gap-2">
            <div className="input-group">
              <span className="input-group-text">🔎</span>
              <input
                className="form-control"
                placeholder="Search application..."
                value={q}
                onChange={(e) => setQ(e.target.value)}
              />
            </div>
          </div>
        </div>

        {loading && (
          <div className="alert alert-info py-2 mb-3">Loading applications...</div>
        )}
        {error && <div className="alert alert-danger py-2 mb-3">{error}</div>}

        <div className="table-responsive">
          <table className="table align-middle">
            <thead className="table-light">
              <tr>
                <th style={{ width: 48 }}>
                  <input className="form-check-input" type="checkbox" />
                </th>
                <th>Applicant</th>
                <th>Job Title</th>
                <th>Submitted</th>
                <th>Status</th>
                <th style={{ width: 60 }}></th>
              </tr>
            </thead>

            <tbody>
              {rows.map((x) => (
                <tr key={x.id}>
                  <td>
                    <input className="form-check-input" type="checkbox" />
                  </td>
                  <td className="fw-semibold">{x.applicantName}</td>
                  <td>{x.jobTitle}</td>
                  <td>{x.submittedAt}</td>
                  <td>
                    <span className="badge text-bg-info">{x.status}</span>
                  </td>
                  <td className="text-end">
                    <button className="btn btn-sm btn-outline-secondary">⋮</button>
                  </td>
                </tr>
              ))}

              {!loading && !rows.length && (
                <tr>
                  <td colSpan={6} className="text-center text-muted py-4">
                    No applications found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="d-flex justify-content-end text-muted small">
          Showing {rows.length} row(s)
        </div>
      </div>
    </div>
  );
}
