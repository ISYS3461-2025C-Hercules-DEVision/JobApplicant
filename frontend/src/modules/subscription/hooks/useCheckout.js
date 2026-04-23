// frontend/src/modules/subscription/hooks/useCheckout.js // File location comment
import { useSelector } from "react-redux"; // Redux hook for state access
import { subscriptionService } from "../services/subscriptionService"; // API client for subscriptions
import { useNavigate } from "react-router-dom"; // Router hook for navigation

export function useCheckout() { // Custom hook for Stripe checkout flow
  const { user } = useSelector((state) => state.auth); // Read auth user from store
  const applicantId = user?.applicantId; // Extract applicant id
  const navigate = useNavigate(); // Navigation helper

  const startCheckout = async () => { // Start checkout handler
    if (!applicantId) { // Require logged-in user
      navigate(`/login`); // Redirect to login
      return; // Stop flow
    }
    try { // Begin API call
      const res = await subscriptionService.createCheckoutSession(applicantId, user?.email); // Create Stripe session
      const redirectUrl = res?.checkoutUrl || res?.paymentUrl || res?.stripeUrl; // Resolve redirect URL
      if (redirectUrl) { // If Stripe URL returned
        sessionStorage.setItem("postPaymentReturn", "/subscription"); // Remember return path
        window.location.href = redirectUrl; // Redirect to Stripe Checkout
        return; // Stop further execution
      }
      throw new Error("No checkout URL returned"); // Fail if no URL
    } catch (e) { // Handle errors
      console.error("Checkout initiation failed", e); // Log error
      // fallback to subscription page
      navigate(`/subscription`); // Return to subscription page
    }
  }; // End startCheckout

  return { startCheckout }; // Expose handler
}
