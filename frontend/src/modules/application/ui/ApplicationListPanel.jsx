import React, { useMemo } from "react";
import ApplicationCard from "./ApplicationCard";
import { useApplicantApplications } from "../hooks/useApplication.js";

function getApplicantIdFromStorage() {
    try {
        const raw = localStorage.getItem("user");
        const user = raw ? JSON.parse(raw) : null;
        return user?.applicantId || "";
    } catch {
        return "";
    }
}

function ApplicationListPanel({ selectedApp, onSelectApp }) {
    const applicantId = useMemo(() => getApplicantIdFromStorage(), []);

    const { applications, loading, error, refetch } =
        useApplicantApplications(applicantId);

    if (loading) {
        return (
            <div className="border-4 border-black p-6 bg-white font-black uppercase">
                Loading applications...
            </div>
        );
    }

    if (!applicantId) {
        return (
            <div className="border-4 border-black p-6 bg-white font-black uppercase text-center">
                Missing applicantId. Please login again.
            </div>
        );
    }

    if (error) {
        return (
            <div className="border-4 border-red-600 p-6 bg-red-50 text-red-800 font-black space-y-4">
                <div>{error}</div>
                <button
                    type="button"
                    className="px-6 py-3 border-4 border-black font-black bg-white hover:bg-black hover:text-white transition-none"
                    onClick={refetch}
                >
                    Retry
                </button>
            </div>
        );
    }

    return (
        <div className="space-y-4">
            {applications.length === 0 ? (
                <div className="border-4 border-black p-6 bg-white font-black uppercase text-center">
                    No applications found.
                </div>
            ) : (
                applications.map((app) => (
                    <ApplicationCard
                        key={app.applicationId}
                        app={app}
                        isSelected={selectedApp?.applicationId === app.applicationId}
                        onClick={() => onSelectApp(app)}
                    />
                ))
            )}
        </div>
    );
}

export default ApplicationListPanel;
