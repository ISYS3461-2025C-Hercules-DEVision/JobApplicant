import ApplicationCard from "./ApplicationCard";

function ApplicationListPanel({ selectedApp, onSelectApp }) {

  const applications = [
    {
      id: 1,
      jobTitle: "Backend Engineer",
      company: "TechNova Solutions",
      location: "Ho Chi Minh City (On-site)",
      appliedDate: "3 days ago",
      status: "In Review",
    },
    {
      id: 2,
      jobTitle: "Frontend Developer (React)",
      company: "BrightWave Digital",
      location: "Da Nang (Hybrid)",
      appliedDate: "1 day ago",
      status: "Submitted",
    },
    {
      id: 3,
      jobTitle: "Data Engineer",
      company: "SkyTech Analytics",
      location: "Remote",
      appliedDate: "5 days ago",
      status: "Rejected",
    },
    {
      id: 4,
      jobTitle: "Mobile Developer (Flutter)",
      company: "DreamLab Studio",
      location: "Ho Chi Minh City",
      appliedDate: "7 days ago",
      status: "Interview",
    },
  ];

  return (
    <div className="space-y-4">
      {applications.map(app => (
        <ApplicationCard 
          key={app.id}
          app={app}
          isSelected={selectedApp?.id === app.id}
          onClick={() => onSelectApp(app)}
        />
      ))}
    </div>
  );
}

export default ApplicationListPanel;
