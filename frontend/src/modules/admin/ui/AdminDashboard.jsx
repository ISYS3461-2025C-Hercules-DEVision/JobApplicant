// src/AdminDashboard.jsx
import React, { useEffect } from "react";
import { Outlet } from "react-router-dom";
import SlideBar from "./componet/Slidebar.jsx";

export default function AdminDashboard() {
    // ✅ load bootstrap only while admin dashboard is mounted
    useEffect(() => {
        const id = "bootstrap-admin-css";

        if (!document.getElementById(id)) {
            const link = document.createElement("link");
            link.id = id;
            link.rel = "stylesheet";
            link.href =
                "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css";
            document.head.appendChild(link);
        }

        // ✅ remove bootstrap when leaving admin
        return () => {
            const el = document.getElementById(id);
            if (el) el.remove();
        };
    }, []);

    const isLoggedIn = true;
    if (!isLoggedIn) return <div className="p-4">Not logged in</div>;

    return (
        <div className="d-flex" style={{ background: "#f6f7fb", minHeight: "100vh" }}>
            {/* Sidebar stays here forever */}
            <SlideBar />

            <main
                className="flex-grow-1"
                style={{
                    backgroundImage:
                        "url('https://assets.softr-files.com/applications/74f47f9a-bc15-42a6-88e7-832982579e12/assets/c16dd521-2113-4761-82a2-c662fa27a56f.svg')",
                }}
            >
                {/* Top Bar */}
                <div className="bg-white border-bottom">
                    <div className="container-fluid py-3 d-flex align-items-center justify-content-between">
                        <div>
                            <div className="fw-bold fs-4">Dashboard</div>
                            <div className="text-muted small">Manage platform</div>
                        </div>

                        <div className="d-flex align-items-center gap-3">
                            <div
                                className="rounded-circle bg-light border d-flex align-items-center justify-content-center"
                                style={{ width: 36, height: 36 }}
                            >
                                🙂
                            </div>
                        </div>
                    </div>
                </div>

                {/* Right side content changes here */}
                <div className="container-fluid py-4">
                    <Outlet />
                </div>
            </main>
        </div>
    );
}
