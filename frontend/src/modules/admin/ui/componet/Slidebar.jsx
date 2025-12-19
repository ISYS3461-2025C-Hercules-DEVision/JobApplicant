// src/components/SlideBar.jsx
import React from "react";
import { NavLink } from "react-router-dom";

export default function SlideBar() {
    const linkClass = ({ isActive }) =>
        `d-flex align-items-center gap-2 px-3 py-2 rounded text-decoration-none ${
            isActive ? "bg-primary text-white" : "text-dark"
        }`;

    return (
        <aside
            className="border-end bg-white"
            style={{ width: 260, minHeight: "100vh", position: "sticky", top: 0 }}
        >
            <div className="p-3 border-bottom">
                <div className="fw-bold fs-5">Admin Panel</div>
                <div className="text-muted small">DeVision</div>
            </div>

            <nav className="p-2 d-grid gap-1">
                <NavLink to="/adminDashboard/adminApplicants" className={linkClass}>
                    <span className="badge text-bg-light">👤</span>
                    Applicant Account
                </NavLink>

                <NavLink to="/adminDashboard/adminCompanies" className={linkClass}>
                    <span className="badge text-bg-light">🏢</span>
                    Company Account
                </NavLink>

                <NavLink to="/adminDashboard/adminApplications" className={linkClass}>
                    <span className="badge text-bg-light">📄</span>
                    Application (CV)
                </NavLink>

                <NavLink to="/adminDashboard/adminJobs" className={linkClass}>
                    <span className="badge text-bg-light">🧾</span>
                    Job Post
                </NavLink>
            </nav>
        </aside>
    );
}
