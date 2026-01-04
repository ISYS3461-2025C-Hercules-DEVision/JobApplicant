import { useEffect, useMemo, useState } from "react";
import HomeNavbar from "../components/Navbar/HomeNavbar";
import FooterSection from "../components/Footer/FooterSection";

import useApplication from "@/modules/application/hooks/useApplication";
import ApplicationListPanel from "../modules/application/ui/ApplicationListPanel";
import ApplicationDetailPanel from "../modules/application/ui/ApplicationDetailPanel";

function ApplicationPage() {
  const { applications, loading } = useApplication();
  const [selectedApp, setSelectedApp] = useState(null);

  // Auto-select first app when data loads (optional but nice UX)
  useEffect(() => {
    if (!loading && applications && applications.length > 0) {
      // keep current selection if still exists
      if (selectedApp) {
        const stillExists = applications.some(
          (a) => (a.applicationId ?? a.id) === (selectedApp.applicationId ?? selectedApp.id)
        );
        if (stillExists) return;
      }
      setSelectedApp(applications[0]);
    }

    if (!loading && (!applications || applications.length === 0)) {
      setSelectedApp(null);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loading, applications]);

  // If you want detail to always use the latest object from list (avoid stale object)
  const selectedFromList = useMemo(() => {
    if (!selectedApp) return null;
    const key = selectedApp.applicationId ?? selectedApp.id;
    return applications?.find((a) => (a.applicationId ?? a.id) === key) ?? selectedApp;
  }, [applications, selectedApp]);

  return (
    <div className="min-h-screen bg-light-gray flex flex-col">
      {/* Header */}
      <HomeNavbar />

      {/* Main Content */}
      <main className="flex-grow max-w-7xl mx-auto w-full px-4 py-6 grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* LEFT PANEL – APPLICATION LIST */}
        <div className="lg:col-span-1 h-[80vh] overflow-y-auto pr-1">
          <ApplicationListPanel
            applications={applications}
            loading={loading}
            selectedApp={selectedApp}
            onSelectApp={setSelectedApp}
          />
        </div>

        {/* RIGHT PANEL – APPLICATION DETAILS */}
        <div className="lg:col-span-2">
          <ApplicationDetailPanel application={selectedFromList} loading={loading} />
        </div>
      </main>

      {/* Footer */}
      <FooterSection />
    </div>
  );
}

export default ApplicationPage;
