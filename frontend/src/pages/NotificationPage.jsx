import { Navigate } from "react-router-dom";
import HomeNavbar from "../components/Navbar/HomeNavbar";
import FooterSection from "../components/Footer/FooterSection";
import NotificationPanel from "../modules/notification/ui/NotificationPanel";

function NotificationPage() {
  // TODO: replace with real auth / subscription state
  const isPremium = true;

  if (!isPremium) {
    return <Navigate to="/subscription" replace />;
  }

  return (
    <div className="min-h-screen bg-light-gray flex flex-col">
      <HomeNavbar />

      <main className="flex-grow max-w-5xl mx-auto w-full px-4 py-10">
        <h1 className="text-5xl font-black uppercase mb-10 text-center">
          Job Notifications
        </h1>

        <NotificationPanel />
      </main>

      <FooterSection />
    </div>
  );
}

export default NotificationPage;
