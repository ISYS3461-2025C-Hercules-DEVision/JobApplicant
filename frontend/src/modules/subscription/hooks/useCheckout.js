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

    const res = await subscriptionService.createCheckoutSession(applicantId);

    // mock payment page
    navigate(`/payment/mock?paymentId=${res.paymentId}`);
  };

  return { startCheckout };
}
