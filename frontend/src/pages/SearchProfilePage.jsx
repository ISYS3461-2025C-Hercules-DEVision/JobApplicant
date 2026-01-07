import { Navigate } from "react-router-dom";
import HomeNavbar from "../components/Navbar/HomeNavbar";
import { useSubscription } from "../modules/subscription/hooks/useSubscription";
import FooterSection from "../components/Footer/FooterSection";
import SearchProfileForm from "../modules/searchProfile/ui/SearchProfileForm";
import ProfileNavBar from "../components/Navbar/ProfileNavBar";

function SearchProfilePage() {
  const { isPremium, loading } = useSubscription();

  if (loading) {
    return <div className="text-center mt-20 font-black">Loading...</div>;
  }

  // Allow viewing the page even if not premium; form will adapt button.

  return (
    <div className="min-h-screen bg-light-gray flex flex-col">
      <ProfileNavBar />

      <main className="flex-grow max-w-4xl mx-auto w-full px-4 py-10">
        <h1 className="text-5xl font-black uppercase mb-10 text-center">
          Search Profile
        </h1>

        {!isPremium && (
          <div className="mb-6 p-4 border-4 border-black bg-yellow-100 font-black text-center">
            Upgrade to Premium to save your Search Profile.
          </div>
        )}

        <SearchProfileForm isPremium={isPremium} />
      </main>

      <FooterSection />
    </div>
  );
}

export default SearchProfilePage;
