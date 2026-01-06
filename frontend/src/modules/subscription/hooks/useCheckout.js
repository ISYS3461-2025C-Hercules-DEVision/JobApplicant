// frontend/src/modules/subscription/hooks/useCheckout.js
import { useNavigate } from "react-router-dom";
import { subscriptionService } from "../services/subscriptionService";

export function useCheckout() {
  const navigate = useNavigate();

  const startCheckout = async () => {
    const res = await subscriptionService.createCheckoutSession();

    // res = { paymentId, status, message }
    navigate(`/payment/mock?paymentId=${res.paymentId}`);
  };

  return { startCheckout };
}
