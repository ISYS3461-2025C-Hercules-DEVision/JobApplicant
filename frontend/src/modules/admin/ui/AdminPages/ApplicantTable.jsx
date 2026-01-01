import React, { useMemo, useState } from "react";
import { useAdmin } from "../../hooks/useAdmin";

export default function ApplicantTable() {
    const [q, setQ] = useState("");

    const {
        applicants,
        loadingApplicants,
        applicantsError,
        fetchApplicants,
        activate,
        deactivate,
        loadingToggleId,
    } = useAdmin();

    const rows = useMemo(() => {
        const list = (applicants || []).map((a) => ({
            applicantId: a.applicantId,
            fullName: a.fullName ?? "—",
            email: a.email ?? "—",
            country: a.country ?? "—",
            isActivated: Boolean(a.isActivated),
        }));

        const s = q.trim().toLowerCase();
        if (!s) return list;

        return list.filter(
            (a) =>
                a.fullName.toLowerCase().includes(s) ||
                a.email.toLowerCase().includes(s) ||
                a.country.toLowerCase().includes(s)
        );
    }, [q, applicants]);

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
                                placeholder="Search by name/email/country..."
                                value={q}
                                onChange={(e) => setQ(e.target.value)}
                            />
                        </div>

                        <button
                            className="btn btn-outline-primary"
                            onClick={fetchApplicants}
                            disabled={loadingApplicants}
                        >
                            {loadingApplicants ? "Refreshing..." : "Refresh"}
                        </button>
                    </div>
                </div>

                {loadingApplicants && (
                    <div className="alert alert-info py-2 mb-3">
                        Loading applicants...
                    </div>
                )}

                {applicantsError && (
                    <div className="alert alert-danger py-2 mb-3">{applicantsError}</div>
                )}

                <div className="table-responsive">
                    <table className="table align-middle">
                        <thead className="table-light">
                        <tr>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Country</th>
                            <th>Activated</th>
                            <th className="text-end" style={{ width: 160 }}>
                                Action
                            </th>
                        </tr>
                        </thead>

                        <tbody>
                        {rows.map((a) => {

                            const isUpdating = loadingToggleId === a.applicantId;

                            return (
                                <tr key={a.applicantId}>
                                    <td className="fw-semibold">{a.fullName}</td>
                                    <td>{a.email}</td>
                                    <td>{a.country}</td>
                                    <td>
                                        {a.isActivated ? (
                                            <span className="badge text-bg-success">Activated</span>
                                        ) : (
                                            <span className="badge text-bg-secondary">Inactive</span>
                                        )}
                                    </td>

                                    <td className="text-end">
                                        {a.isActivated ? (
                                            <button
                                                className="btn btn-sm btn-outline-danger"
                                                disabled={isUpdating}
                                                onClick={() => deactivate(a.applicantId)}
                                            >
                                                {isUpdating ? "Deactivating..." : "Deactivate"}
                                            </button>
                                        ) : (
                                            <button
                                                className="btn btn-sm btn-outline-success"
                                                disabled={isUpdating}
                                                onClick={() => activate(a.applicantId)}
                                            >
                                                {isUpdating ? "Activating..." : "Activate"}
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            );
                        })}

                        {!loadingApplicants && !rows.length && (
                            <tr>
                                <td colSpan={5} className="text-center text-muted py-4">
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
