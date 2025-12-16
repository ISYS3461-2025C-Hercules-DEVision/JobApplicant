function ApplicationCard({ app, isSelected, onClick }) {

  // Status color logic
  const statusColors = {
    "Submitted": "text-blue-600",
    "In Review": "text-yellow-600",
    "Interview": "text-green-600",
    "Offer": "text-green-700",
    "Rejected": "text-gray-500",
  };

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
      <h3 className="text-lg font-black">{app.jobTitle}</h3>
      <p className="font-bold">{app.company}</p>
      <p className="text-sm">{app.location}</p>

      <p className="mt-2 font-bold">
        Applied: {app.appliedDate}
      </p>

      <p className={`
        mt-1 font-black uppercase 
        ${isSelected ? "text-black" : statusColors[app.status]}
      `}>
        Status: {app.status}
      </p>
    </div>
  );
}

export default ApplicationCard;
