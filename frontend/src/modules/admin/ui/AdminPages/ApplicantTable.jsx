// src/pages/ApplicantAccount.jsx
import React, { useMemo, useState } from "react";
import { applicants as mockApplicants } from "../data/mockData";

export default function ApplicantTable() {
    const [q, setQ] = useState("");

    const rows = useMemo(() => {
        const s = q.trim().toLowerCase();
        if (!s) return mockApplicants;
        return mockApplicants.filter(
            (a) =>
                a.name.toLowerCase().includes(s) ||
                a.email.toLowerCase().includes(s) ||
                a.phone.toLowerCase().includes(s) ||
                a.status.toLowerCase().includes(s)
        );
    }, [q]);

    return (
        <div className="card shadow-sm border-0">
            <div className="card-body">
                <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
                    <h5 className="mb-0">Applicant Accounts</h5>

                    <div className="d-flex gap-2">
                        <div className="input-group">
                            <span className="input-group-text">🔎</span>
                            <input
                                className="form-control"
                                placeholder="Search applicant..."
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
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Verified</th>
                            <th>Status</th>
                            <th style={{ width: 60 }}></th>
                        </tr>
                        </thead>
                        <tbody>
                        {rows.map((a) => (
                            <tr key={a.id}>
                                <td>
                                    <input className="form-check-input" type="checkbox" />
                                </td>
                                <td className="fw-semibold">{a.name}</td>
                                <td>{a.email}</td>
                                <td>{a.phone}</td>
                                <td>
                                    {a.verified ? (
                                        <span className="badge text-bg-success">✔</span>
                                    ) : (
                                        <span className="badge text-bg-secondary">—</span>
                                    )}
                                </td>
                                <td>
                    <span
                        className={`badge ${
                            a.status === "Active" ? "text-bg-success" : "text-bg-danger"
                        }`}
                    >
                      {a.status}
                    </span>
                                </td>
                                <td className="text-end">
                                    <button className="btn btn-sm btn-outline-secondary">⋮</button>
                                </td>
                            </tr>
                        ))}
                        {!rows.length && (
                            <tr>
                                <td colSpan={7} className="text-center text-muted py-4">
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
