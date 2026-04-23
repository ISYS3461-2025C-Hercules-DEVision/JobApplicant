// frontend/src/modules/subscription/hooks/useSubscription.js // File location comment
import { useEffect, useState } from "react"; // React hooks
import { useSelector } from "react-redux"; // Redux hook for state access
import { subscriptionService } from "../services/subscriptionService"; // API client for subscriptions
import { onSubscriptionUpdated, offSubscriptionUpdated } from "../events/subscriptionEvents"; // Event bus helpers

export function useSubscription() { // Custom hook for subscription state
  const { user } = useSelector((state) => state.auth); // Read auth user from store
  const applicantId = user?.applicantId; // Extract applicant id

  const [subscription, setSubscription] = useState(null); // Current subscription data
  const [loading, setLoading] = useState(true); // Loading flag

  useEffect(() => { // Fetch subscription on user change
    if (!applicantId) return; // Skip if not logged in

    subscriptionService // Call backend
      .getMySubscription(applicantId) // Fetch subscription
      .then((res) => setSubscription(res)) // Store result
      .finally(() => setLoading(false)); // Stop loading
  }, [applicantId]); // Re-run when applicant changes

  // Listen for subscription updates and refetch
  useEffect(() => { // Subscribe to update events
    const handler = () => { // Event handler
      if (!applicantId) return; // Skip if not logged in
      subscriptionService // Call backend
        .getMySubscription(applicantId) // Refetch status
        .then((res) => setSubscription(res)) // Update state
        .catch(() => {}); // Ignore errors silently
    }; // End handler
    onSubscriptionUpdated(handler); // Register handler
    return () => offSubscriptionUpdated(handler); // Cleanup on unmount
  }, [applicantId]); // Re-register if applicant changes

  return { // Hook return object
    subscription, // Subscription data
    isPremium: subscription?.planType === "PREMIUM" && subscription?.active === true, // Premium flag
    loading, // Loading flag
  };
}


