import React, { useEffect, useMemo, useState } from "react";
import * as adminService from "../../services/adminService.js";

const MOCK_COMPANIES = [
  {
    id: "mock-1",
    name: "Mock Company A",
    email: "contact@mocka.com",
    status: "ACTIVE",
    createdAt: "2024-01-01",
  },
  {
    id: "mock-2",
    name: "Mock Company B",
    email: "contact@mockb.com",
    status: "INACTIVE",
    createdAt: "2024-01-02",
  },
];

function normalizeList(data) {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.data)) return data.data;
  if (Array.isArray(data?.content)) return data.content;
  return [];
}

export default function CompanyTable() {
  const [q, setQ] = useState("");

  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  const handleToggleStatus = async (companyRow) => {
    const companyId = companyRow.id;

    const isActive = String(companyRow.status).toLowerCase() === "active";
    const action = isActive ? "deactivate" : "activate";

    const ok = window.confirm(
      `${isActive ? "Deactivate" : "Activate"} this company?`
    );
    if (!ok) return;

    try {
      if (isActive) {
        await adminService.deactivateCompany(companyId);
      } else {
        await adminService.activateCompany(companyId);
      }

      // Update UI (companies raw list) safely
      setCompanies((prev) =>
        prev.map((c) => {
          const id = String(c.companyId ?? c.id ?? "");
          if (id !== String(companyId)) return c;

          // Update status on raw object so your rows useMemo will reflect it
          return {
            ...c,
            status: isActive ? "INACTIVE" : "ACTIVE",
            active: !isActive, // optional: if backend uses boolean
            activated: !isActive, // optional
          };
        })
      );
    } catch (error) {
      console.error("Toggle company status failed", error);
      alert(error?.message || "Toggle company status failed");
    }
  };

  useEffect(() => {
    let mounted = true;

    (async () => {
      setLoading(true);
      setError(null);

      try {
        const res = await adminService.getAllCompaniesFromJM({
          page: 1,
          size: 200,
        });

        if (!mounted) return;

        setCompanies(normalizeList(res));
      } catch (err) {
        if (!mounted) return;

        console.error("Fetch companies from JM failed, using mock data", err);

        setError(err?.message || "Failed to load companies from JM");
        setCompanies(MOCK_COMPANIES);
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

    const mapped = companies.map((c) => {
      const id = c.companyId ?? c.id ?? "-";
      const companyName =
        c.companyName ?? c.name ?? c.company ?? c.displayName ?? "-";

      const email =
        c.email ?? c.contactEmail ?? c.hrEmail ?? "-";

      const industry =
        c.industry ?? c.sector ?? c.field ?? c.category ?? "-";

      // status normalization
      const statusRaw = c.status ?? c.active ?? c.activated;
      const status =
        typeof statusRaw === "boolean"
          ? statusRaw
            ? "Active"
            : "Inactive"
          : String(statusRaw ?? "Active");

      return {
        id: String(id),
        companyName: String(companyName),
        email: String(email),
        industry: String(industry),
        status: String(status),
        _raw: c,
      };
    });

    if (!s) return mapped;

    return mapped.filter(
      (c) =>
        c.companyName.toLowerCase().includes(s) ||
        c.email.toLowerCase().includes(s) ||
        c.industry.toLowerCase().includes(s) ||
        c.status.toLowerCase().includes(s)
    );
  }, [q, companies]);

  return (
    <div className="card shadow-sm border-0">
      <div className="card-body">
        <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
          <h5 className="mb-0">Companies</h5>

          <div className="d-flex gap-2">
            <div className="input-group">
              <span className="input-group-text">🔎</span>
              <input
                className="form-control"
                placeholder="Search company..."
                value={q}
                onChange={(e) => setQ(e.target.value)}
              />
            </div>
          </div>
        </div>

        {loading && (
          <div className="alert alert-info py-2 mb-3">
            Loading companies...
          </div>
        )}
        {error && (
          <div className="alert alert-danger py-2 mb-3">{error}</div>
        )}

        <div className="table-responsive">
          <table className="table align-middle">
            <thead className="table-light">
              <tr>
                <th style={{ width: 48 }}>
                  <input className="form-check-input" type="checkbox" />
                </th>
                <th>Company</th>
                <th>Email</th>
                <th>Industry</th>
                <th>Status</th>
                <th style={{ width: 60 }}></th>
              </tr>
            </thead>

            <tbody>
              {rows.map((c) => (
                <tr key={c.id}>
                  <td>
                    <input className="form-check-input" type="checkbox" />
                  </td>
                  <td className="fw-semibold">{c.companyName}</td>
                  <td>{c.email}</td>
                  <td>{c.industry}</td>
                  <td>
                    <span
                      className={`badge ${
                        c.status === "Active"
                          ? "text-bg-success"
                          : "text-bg-secondary"
                      }`}
                    >
                      {c.status}
                    </span>
                  </td>
                  <td className="text-end">
                    <button
                      className={`btn btn-sm ${
                        c.status === "Active" ? "btn-outline-warning" : "btn-outline-success"
                      }`}
                      disabled={loading}
                      onClick={() => handleToggleStatus(c)}
                    >
                      {c.status === "Active" ? "Deactivate" : "Activate"}
                    </button>
                  </td>
                </tr>
              ))}

              {!loading && !rows.length && (
                <tr>
                  <td
                    colSpan={6}
                    className="text-center text-muted py-4"
                  >
                    No companies found.
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
