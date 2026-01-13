// frontend/src/modules/subscription/hooks/useCheckout.js
import { useSelector } from "react-redux";
import { subscriptionService } from "../services/subscriptionService";
import { useNavigate } from "react-router-dom";

export function useCheckout() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;
  const navigate = useNavigate();

  const startCheckout = async () => {
    if (!applicantId) return;
    try {
      const res = await subscriptionService.createCheckoutSession(applicantId, user?.email);
      const redirectUrl = res?.checkoutUrl || res?.paymentUrl || res?.stripeUrl;
      // If backend provides Stripe URL, redirect there
      if (redirectUrl) {
        sessionStorage.setItem("postPaymentReturn", "/subscription");
        window.location.href = redirectUrl;
        return;
      }
      // Fallback: if mock success was processed locally
      if (res?.status === "SUCCESS") {
        navigate("/subscription");
        return;
      }
      // Otherwise, keep user on page
      alert(res?.message || "Unable to start checkout");
    } catch (e) {
      console.error(e);
      alert("Failed to start checkout");
    }
  };

  return { startCheckout };
}
