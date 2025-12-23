import HomeNavbar from "../components/Navbar/HomeNavbar";
import FooterSection from "../components/Footer/FooterSection";

import PlanCard from "../modules/subscription/ui/PlanCard";
import SubscribeButton from "../modules/subscription/ui/SubscribeButton";

import { useSubscription } from "../modules/subscription/hooks/useSubscription";
import { useCheckout } from "../modules/subscription/hooks/useCheckout";

function SubscriptionPage() {
  const { subscription, isPremium, loading } = useSubscription();
  const { startCheckout } = useCheckout();

  if (loading) {
    return <div className="text-center mt-20 font-black">Loading...</div>;
  }

  return (
    <div className="min-h-screen bg-light-gray flex flex-col">
      <HomeNavbar />

      <main className="flex-grow max-w-6xl mx-auto w-full px-4 py-10">
        <h1 className="text-5xl font-black uppercase mb-10 text-center">
          Subscription Plans
        </h1>

        <div className="mb-8 text-center">
          <span className="inline-block px-6 py-2 border-4 border-black font-black">
            {isPremium ? "PREMIUM ACTIVE" : "FREE PLAN"}
          </span>
        </div>

        <div className="grid md:grid-cols-2 gap-8">
          <PlanCard
            title="Free Plan"
            price="$0"
            features={[
              "Browse job listings",
              "Apply to jobs",
              "Save jobs",
              "Basic job search",
            ]}
            footer={
              <span className="inline-block px-4 py-2 border-4 border-black font-black">
                CURRENT PLAN
              </span>
            }
          />

          <PlanCard
            title="Premium Applicant"
            price="$10"
            highlight
            features={[
              "Real-time job notifications",
              "Smart job matching",
              "Search profile automation",
              "Priority application visibility",
            ]}
          />
        </div>

        {!isPremium && (
          <SubscribeButton onClick={startCheckout} />
        )}

        {isPremium && subscription?.expiryDate && (
          <div className="text-center mt-8 font-black">
            Premium valid until{" "}
            {new Date(subscription.expiryDate).toLocaleDateString()}
          </div>
        )}
      </main>

      <FooterSection />
    </div>
  );
}

export default SubscriptionPage;
