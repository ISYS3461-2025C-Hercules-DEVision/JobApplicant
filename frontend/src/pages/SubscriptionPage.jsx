import HomeNavbar from "../components/Navbar/HomeNavbar";
import FooterSection from "../components/Footer/FooterSection";
import PlanCard from "../modules/subscription/ui/PlanCard";
import SubscribeButton from "../modules/subscription/ui/SubscribeButton";
import { useSubscription } from "../modules/subscription/hooks/useSubscription";
import { useCheckout } from "../modules/subscription/hooks/useCheckout";
import { useSelector } from "react-redux";
import { subscriptionService } from "../modules/subscription/services/subscriptionService";
import { emitSubscriptionUpdated } from "../modules/subscription/events/subscriptionEvents";

function SubscriptionPage() {
  const { subscription, isPremium, loading } = useSubscription();
  const { startCheckout } = useCheckout();
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  if (loading) {
    return <div className="text-center mt-20 font-black">Loading...</div>;
  }

  return (
    <div className="min-h-screen bg-light-gray flex flex-col">
      <HomeNavbar />

      <main className="flex-grow max-w-6xl mx-auto px-4 py-10">
        <h1 className="text-5xl font-black text-center mb-10">
          Subscription Plans
        </h1>

        <div className="grid md:grid-cols-2 gap-8">
          <PlanCard
            title="Free Plan"
            price="$0"
            features={["Browse jobs", "Apply to jobs", "Save jobs"]}
            footer={
              !isPremium ? (
                <span className="border-4 border-black px-4 py-2 font-black">
                  CURRENT PLAN
                </span>
              ) : null
            }
          />

          <PlanCard
            title="Premium Applicant"
            price="$10 / month"
            highlight
            features={[
              "Real-time notifications",
              "Priority visibility",
              "Smart job matching",
            ]}
            footer={
              isPremium ? (
                <div>
                  <span className="border-4 border-black px-4 py-2 font-black">
                    CURRENT PLAN
                  </span>
                  {subscription?.expiryDate && (
                    <div className="mt-2 py-2 text-sm font-black">
                      Valid until{" "}
                      {new Date(subscription.expiryDate).toLocaleDateString()}
                    </div>
                  )}
                </div>
              ) : null
            }
          />
        </div>

        <SubscribeButton
          onClick={async () => {
            if (isPremium) {
              try {
                if (!applicantId) return;
                await subscriptionService.cancelSubscription(applicantId);
                emitSubscriptionUpdated();
              } catch (e) {
                console.error("Cancel subscription failed", e);
              }
            } else {
              startCheckout();
            }
          }}
          label={isPremium ? "CANCEL SUBSCRIPTION" : "SUBSCRIBE NOW"}
        />
      </main>

      <FooterSection />
    </div>
  );
}

export default SubscriptionPage;
