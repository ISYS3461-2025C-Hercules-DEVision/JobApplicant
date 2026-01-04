function ApplicationCard({ app, isSelected, onClick }) {
  // Map backend enum -> label giống UI
  const statusLabelMap = {
    SUBMITTED: "Submitted",
    IN_REVIEW: "In Review",
    INTERVIEW: "Interview",
    OFFER: "Offer",
    REJECTED: "Rejected",
  };

  // Status color logic (giữ như cũ)
  const statusColors = {
    Submitted: "text-blue-600",
    "In Review": "text-yellow-600",
    Interview: "text-green-600",
    Offer: "text-green-700",
    Rejected: "text-gray-500",
  };

  const rawStatus = typeof app.status === "string" ? app.status : String(app.status ?? "");
  const statusLabel = statusLabelMap[rawStatus] ?? rawStatus ?? "N/A";

  const appliedDate = formatRelativeTime(app.createdAt);

  // vì backend chưa có jobTitle/companyName/location => show IDs
  const title = app.jobPostTitle || `JobPost: ${app.jobPostId ?? "N/A"}`;
  const companyLine = app.companyName || `Company: ${app.companyId ?? "N/A"}`;
  const locationLine = app.location || `Application ID: ${app.applicationId ?? app.id ?? "N/A"}`;

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
      <h3 className="text-lg font-black">{title}</h3>
      <p className="font-bold">{companyLine}</p>
      <p className="text-sm">{locationLine}</p>

      <p className="mt-2 font-bold">Applied: {appliedDate}</p>

      <p
        className={`
          mt-1 font-black uppercase 
          ${isSelected ? "text-black" : statusColors[statusLabel] || "text-gray-700"}
        `}
      >
        Status: {statusLabel}
      </p>
    </div>
  );
}

// Helpers
function formatRelativeTime(isoString) {
  if (!isoString) return "N/A";
  const t = new Date(isoString).getTime();
  if (Number.isNaN(t)) return "N/A";

  const diffMs = Date.now() - t;
  const diffMin = Math.floor(diffMs / (60 * 1000));
  const diffHour = Math.floor(diffMs / (60 * 60 * 1000));
  const diffDay = Math.floor(diffMs / (24 * 60 * 60 * 1000));

  if (diffMin < 1) return "just now";
  if (diffMin < 60) return `${diffMin} min ago`;
  if (diffHour < 24) return `${diffHour} hours ago`;
  return `${diffDay} days ago`;
}

export default ApplicationCard;
