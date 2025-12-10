import { useState } from "react";
import HomeNavbar from "../components/Navbar/HomeNavbar";
import FooterSection from "../components/Footer/FooterSection";

import JobListPanel from "../modules/job/ui/JobListPanel";
import JobDetailPanel from "../modules/job/ui/JobDetailPanel";

function JobListPage() {
  const [selectedJob, setSelectedJob] = useState(null);

  return (
    <div className="min-h-screen bg-light-gray flex flex-col">

      {/* Header */}
      <HomeNavbar />

      {/* Main Content */}
      <main className="flex-grow max-w-7xl mx-auto w-full px-4 py-6 grid grid-cols-1 lg:grid-cols-3 gap-6">

        {/* LEFT — Job List */}
        <div className="lg:col-span-1 h-[80vh] overflow-y-auto pr-1">
          <JobListPanel onSelectJob={setSelectedJob} selectedJob={selectedJob} />
        </div>

        {/* RIGHT — Job Detail */}
        <div className="lg:col-span-2">
          <JobDetailPanel job={selectedJob} />
        </div>

      </main>

      {/* Footer */}
      <FooterSection />

    </div>
  );
}

export default JobListPage;
