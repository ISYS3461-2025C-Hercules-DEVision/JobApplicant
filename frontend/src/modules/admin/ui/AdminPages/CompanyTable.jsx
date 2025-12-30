// src/pages/CompanyAccount.jsx
import React, { useMemo, useState } from "react";
import { companies as mockCompanies } from "../data/mockData";

export default function CompanyTable() {
    const [q, setQ] = useState("");

    const rows = useMemo(() => {
        const s = q.trim().toLowerCase();
        if (!s) return mockCompanies;
        return mockCompanies.filter(
            (c) =>
                c.companyName.toLowerCase().includes(s) ||
                c.email.toLowerCase().includes(s) ||
                c.industry.toLowerCase().includes(s) ||
                c.status.toLowerCase().includes(s)
        );
    }, [q]);

    return (
        <div className="card shadow-sm border-0">
            <div className="card-body">
                <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
                    <h5 className="mb-0">Company Accounts</h5>

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
                            c.status === "Active" ? "text-bg-success" : "text-bg-secondary"
                        }`}
                    >
                      {c.status}
                    </span>
                                </td>
                                <td className="text-end">
                                    <button className="btn btn-sm btn-outline-secondary">⋮</button>
                                </td>
                            </tr>
                        ))}
                        {!rows.length && (
                            <tr>
                                <td colSpan={6} className="text-center text-muted py-4">
                                    No results.
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
