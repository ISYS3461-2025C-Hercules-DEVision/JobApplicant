import { useState } from "react";
import HomeNavbar from "../components/Navbar/HomeNavbar";
import FooterSection from "../components/Footer/FooterSection";

import PlanCard from "../modules/subscription/ui/PlanCard";
import SubscribeButton from "../modules/subscription/ui/SubscribeButton";
import PaymentModal from "../modules/subscription/ui/PaymentModal";

function SubscriptionPage() {
  const [showPayment, setShowPayment] = useState(false);
  const [isPremium, setIsPremium] = useState(false);

  const userEmail = "applicant@email.com";

  const handlePaymentSuccess = () => {
    setIsPremium(true);
    setShowPayment(false);

    console.log("Payment recorded:", {
      email: userEmail,
      time: new Date().toISOString(),
    });
  };

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
          <SubscribeButton onClick={() => setShowPayment(true)} />
        )}

        {isPremium && (
          <div className="text-center mt-8 font-black">
            You are currently on the Premium plan.
          </div>
        )}
      </main>

      {showPayment && (
        <PaymentModal
          email={userEmail}
          onSuccess={handlePaymentSuccess}
          onClose={() => setShowPayment(false)}
        />
      )}

      <FooterSection />
    </div>
  );
}

export default SubscriptionPage;
