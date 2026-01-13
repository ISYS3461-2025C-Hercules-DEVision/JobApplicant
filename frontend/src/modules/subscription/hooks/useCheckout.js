// frontend/src/modules/subscription/hooks/useCheckout.js
import { useSelector } from "react-redux";
import { subscriptionService } from "../services/subscriptionService";
import { useNavigate } from "react-router-dom";

export function useCheckout() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;
  const navigate = useNavigate();

  const startCheckout = async () => {
    if (!applicantId) {
      navigate(`/login`);
      return;
    }
    try {
      const res = await subscriptionService.createCheckoutSession(applicantId, user?.email);
      const redirectUrl = res?.checkoutUrl || res?.paymentUrl || res?.stripeUrl;
      if (redirectUrl) {
        sessionStorage.setItem("postPaymentReturn", "/subscription");
        window.location.href = redirectUrl;
        return;
      }
      throw new Error("No checkout URL returned");
    } catch (e) {
      console.error("Checkout initiation failed", e);
      // fallback to subscription page
      navigate(`/subscription`);
    }
  };

  return { startCheckout };
}
