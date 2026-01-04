import ApplicationCard from "./ApplicationCard";

function ApplicationListPanel({ applications = [], loading = false, selectedApp, onSelectApp }) {
  if (loading) {
    return (
      <div className="space-y-4">
        <div className="bg-white border-4 border-black p-6 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] font-black">
          Loading applications...
        </div>
      </div>
    );
  }

  if (!applications || applications.length === 0) {
    return (
      <div className="bg-white border-4 border-black p-10 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] text-center font-black">
        No applications yet
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {applications.map((app) => {
        const key = app.applicationId ?? app.id;
        const selectedKey = selectedApp?.applicationId ?? selectedApp?.id;

        return (
          <ApplicationCard
            key={key}
            app={app}
            isSelected={selectedKey === key}
            onClick={() => onSelectApp(app)}
          />
        );
      })}
    </div>
  );
}

export default ApplicationListPanel;
