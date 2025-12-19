// src/pages/JobPost.jsx
import React, { useMemo, useState } from "react";
import { jobPosts as mockJobPosts } from "../data/mockData";

export default function JobPostTable() {
    const [q, setQ] = useState("");

    const rows = useMemo(() => {
        const s = q.trim().toLowerCase();
        if (!s) return mockJobPosts;
        return mockJobPosts.filter(
            (j) =>
                j.title.toLowerCase().includes(s) ||
                j.company.toLowerCase().includes(s) ||
                j.location.toLowerCase().includes(s) ||
                j.status.toLowerCase().includes(s)
        );
    }, [q]);

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
