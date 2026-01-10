import React from "react";

/**
 * app is a merged object:
 * {
 *   applicationId,
 *   status (PENDING | VIEWED | ACCEPTED | REJECTED),
 *   submissionDate / createdAt,
 *   job: { title, companyName, location }
 * }
 */
function ApplicationCard({ app, isSelected, onClick }) {
    // Status label mapping
    const statusLabel = {
        PENDING: "Submitted",
        VIEWED: "In Review",
        ACCEPTED: "Accepted",
        REJECTED: "Rejected",
    };

    // Status color mapping
    const statusColors = {
        PENDING: "text-blue-600",
        VIEWED: "text-yellow-600",
        ACCEPTED: "text-green-700",
        REJECTED: "text-gray-500",
    };

    const jobTitle = app?.job?.title || "Unknown Job";
    const company = app?.job?.companyName || "Unknown Company";
    const location = app?.job?.location || "Unknown Location";

    const appliedDate = app?.appliedDateText || "-";
    const status = app?.status || "PENDING";

    return (
        <div
            onClick={onClick}
            className={`
        cursor-pointer border-4 border-black p-4 
        shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]
        hover:translate-x-1 hover:translate-y-1 hover:shadow-none
        transition-none
        
        ${isSelected ? "bg-primary text-white" : "bg-white text-black"}
      `}
        >
            <h3 className="text-lg font-black">{jobTitle}</h3>
            <p className="font-bold">{company}</p>
            <p className="text-sm">{location}</p>

            <p className="mt-2 font-bold">Applied: {appliedDate}</p>

            <p
                className={`
          mt-1 font-black uppercase 
          ${
                    isSelected
                        ? "text-black"
                        : statusColors[status] || "text-gray-700"
                }
        `}
            >
                Status: {statusLabel[status] || status}
            </p>
        </div>
    );
}

export default ApplicationCard;
