// frontend/src/modules/subscription/hooks/useSubscription.js
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { subscriptionService } from "../services/subscriptionService";

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

  return {
    subscription,
    isPremium: subscription?.active === true,
    loading,
  };
}


