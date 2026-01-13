import { useState } from "react";
import HomeNavbar from "../components/Navbar/HomeNavbar";
import FooterSection from "../components/Footer/FooterSection";

import ApplicationListPanel from "../modules/application/ui/ApplicationListPanel";
import ApplicationDetailPanel from "../modules/application/ui/ApplicationDetailPanel";
import {useSelector} from "react-redux";
import ProfileNavBar from "../components/Navbar/ProfileNavBar.jsx";

function ApplicationPage() {
  const [selectedApp, setSelectedApp] = useState(null);

  const {token} = useSelector((state) => state.auth);

  return (
    <div className="min-h-screen bg-light-gray flex flex-col">

      {/* Header */}
        {token ? <ProfileNavBar /> : <HomeNavbar/> }

      {/* Main Content */}
      <main className="flex-grow max-w-7xl mx-auto w-full px-4 py-6 grid grid-cols-1 lg:grid-cols-3 gap-6">

        {/* LEFT PANEL – APPLICATION LIST */}
        <div className="lg:col-span-1 h-[80vh] overflow-y-auto pr-1">
          <ApplicationListPanel
            selectedApp={selectedApp}
            onSelectApp={setSelectedApp}
          />
        </div>

        {/* RIGHT PANEL – APPLICATION DETAILS */}
        <div className="lg:col-span-2">
          <ApplicationDetailPanel application={selectedApp} />
        </div>

      </main>

      {/* Footer */}
      <FooterSection />

    </div>
  );
}

export default ApplicationPage;
