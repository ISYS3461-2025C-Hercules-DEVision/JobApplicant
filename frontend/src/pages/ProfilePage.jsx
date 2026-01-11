import HomeNavbar from "../components/Navbar/HomeNavbar";
import FooterSection from "../components/Footer/FooterSection";

import ProfileHeader from "../modules/profile/ui/ProfileHeader";
import AboutSection from "../modules/profile/ui/AboutSection";
import ActivitySection from "../modules/profile/ui/MediaPortfolioSection.jsx";
import ExperienceSection from "../modules/profile/ui/ExperienceSection";
import EducationSection from "../modules/profile/ui/EducationSection";
import ContactSection from "../modules/profile/ui/ContactSection";
import SkillsSection from "../modules/profile/ui/SkillsSection";
import ProfileNavBar from "../components/Navbar/ProfileNavBar.jsx";
import {useSelector} from "react-redux";
import MediaPortfolioSection from "../modules/profile/ui/MediaPortfolioSection.jsx";
import {useResume} from "../modules/profile/hooks/useResume.js";

function ProfilePage() {
    const {token, user} = useSelector((state) => state.auth);
    const applicantId = user?.applicantId;  //Check if logged in

    const resume = useResume(applicantId);
    console.log("Resume data in Profile Page: ", resume);
    if (!applicantId) {
        return <div>Please log in to view your profile</div>;
    }

  return (
    <div className="min-h-screen bg-light-gray flex flex-col">
        {token ? <ProfileNavBar /> : <HomeNavbar/> }

      {/* Main content container */}
      <main className="flex-grow max-w-7xl mx-auto px-4 py-10 grid grid-cols-1 lg:grid-cols-3 gap-8">

        {/* Left Column */}
        <div className="lg:col-span-2 space-y-8">
          <ProfileHeader/>
          <AboutSection/>
          <MediaPortfolioSection />
          <ExperienceSection />
          <EducationSection/>
        </div>

        {/* Right Column */}
        <div className="space-y-8">
          <ContactSection/>
          <SkillsSection/>
        </div>

      </main>

      <FooterSection />
    </div>
  );
}

export default ProfilePage;
