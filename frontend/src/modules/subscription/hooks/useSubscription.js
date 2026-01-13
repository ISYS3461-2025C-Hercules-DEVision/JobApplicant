// frontend/src/modules/subscription/hooks/useSubscription.js
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { subscriptionService } from "../services/subscriptionService";
import { onSubscriptionUpdated, offSubscriptionUpdated } from "../events/subscriptionEvents";

export function useSubscription() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const [subscription, setSubscription] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!applicantId) return;

    subscriptionService
      .getMySubscription(applicantId)
      .then((res) => setSubscription(res))
      .finally(() => setLoading(false));
  }, [applicantId]);

  // Listen for subscription updates and refetch
  useEffect(() => {
    const handler = () => {
      if (!applicantId) return;
      subscriptionService
        .getMySubscription(applicantId)
        .then((res) => setSubscription(res))
        .catch(() => {});
    };
    onSubscriptionUpdated(handler);
    return () => offSubscriptionUpdated(handler);
  }, [applicantId]);

  return {
    subscription,
    isPremium: subscription?.planType === "PREMIUM" && subscription?.active === true,
    loading,
  };
}


