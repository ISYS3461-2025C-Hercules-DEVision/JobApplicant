import React from "react";
import ApplicationCard from "./ApplicationCard";

function ApplicationListPanel({ applications, selectedApp, onSelectApp, loading, error }) {
    if (loading) {
        return (
            <div className="border-4 border-black p-6 bg-white font-black uppercase">
                Loading applications...
            </div>
        );
    }

    if (error) {
        return (
            <div className="border-4 border-red-600 p-6 bg-red-50 text-red-800 font-black">
                {error}
            </div>
        );
    }

    if (!applications || applications.length === 0) {
        return (
            <div className="border-4 border-black p-6 bg-white font-black uppercase text-center">
                No applications found.
            </div>
        );
    }

    return (
        <div className="space-y-4">
            {applications.map((app) => (
                <ApplicationCard
                    key={app.applicationId}
                    app={app}
                    isSelected={selectedApp?.applicationId === app.applicationId}
                    onClick={() => onSelectApp(app)}
                />
            ))}
        </div>
    );
}

export default ApplicationListPanel;
