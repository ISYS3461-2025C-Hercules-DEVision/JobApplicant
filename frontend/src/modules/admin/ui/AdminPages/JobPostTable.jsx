import React, { useEffect, useMemo, useState } from "react";
import * as adminService from "../../services/adminService.js";

function normalizeList(data) {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.data)) return data.data;
  if (Array.isArray(data?.content)) return data.content;
  return [];
}

export default function JobPostTable() {
  const [q, setQ] = useState("");
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    let mounted = true;

    (async () => {
      setLoading(true);
      setError(null);
      try {
        const res = await adminService.getAllJobsFromJM({ page: 1, size: 200 });
        if (mounted) setJobs(normalizeList(res));
      } catch (err) {
        if (mounted) setError(err?.message || "Failed to load jobs from JM");
      } finally {
        if (mounted) setLoading(false);
      }
    })();

    return () => {
      mounted = false;
    };
  }, []);

  const rows = useMemo(() => {
    const s = q.trim().toLowerCase();

    const mapped = jobs.map((j) => {
      const id = j.jobId ?? j.id ?? j.jobPostId;
      const title = j.title ?? "-";
      const company = j.company ?? j.companyName ?? "-";
      const location = j.location ?? j.country ?? "-";
      const status = j.status ?? "Open"; // nếu JM không trả status thì default Open

      return {
        id: String(id),
        title,
        company,
        location,
        status: String(status),
        _raw: j,
      };
    });

    if (!s) return mapped;

    return mapped.filter(
      (j) =>
        j.title.toLowerCase().includes(s) ||
        j.company.toLowerCase().includes(s) ||
        j.location.toLowerCase().includes(s) ||
        j.status.toLowerCase().includes(s)
    );
  }, [q, jobs]);

  return (
    <div className="card shadow-sm border-0">
      <div className="card-body">
        <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
          <h5 className="mb-0">Job Posts</h5>

          <div className="d-flex gap-2">
            <div className="input-group">
              <span className="input-group-text">🔎</span>
              <input
                className="form-control"
                placeholder="Search job..."
                value={q}
                onChange={(e) => setQ(e.target.value)}
              />
            </div>
          </div>
        </div>

        {loading && <div className="alert alert-info py-2 mb-3">Loading jobs...</div>}
        {error && <div className="alert alert-danger py-2 mb-3">{error}</div>}

        <div className="table-responsive">
          <table className="table align-middle">
            <thead className="table-light">
              <tr>
                <th style={{ width: 48 }}>
                  <input className="form-check-input" type="checkbox" />
                </th>
                <th>Title</th>
                <th>Company</th>
                <th>Location</th>
                <th>Status</th>
                <th style={{ width: 60 }}></th>
              </tr>
            </thead>

            <tbody>
              {rows.map((j) => (
                <tr key={j.id}>
                  <td>
                    <input className="form-check-input" type="checkbox" />
                  </td>

                  <td className="fw-semibold">{j.title}</td>
                  <td>{j.company}</td>
                  <td>{j.location}</td>
                  <td>
                    <span
                      className={`badge ${
                        j.status === "Open" ? "text-bg-success" : "text-bg-secondary"
                      }`}
                    >
                      {j.status}
                    </span>
                  </td>
                  <td className="text-end">
                    <button className="btn btn-sm btn-outline-secondary">⋮</button>
                  </td>
                </tr>
              ))}

              {!loading && !rows.length && (
                <tr>
                  <td colSpan={6} className="text-center text-muted py-4">
                    No job posts found.
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
