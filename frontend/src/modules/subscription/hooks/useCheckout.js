// frontend/src/modules/subscription/hooks/useCheckout.js
import { useSelector } from "react-redux";
import { subscriptionService } from "../services/subscriptionService";
import { useNavigate } from "react-router-dom";

export function useCheckout() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;
  const navigate = useNavigate();

  const startCheckout = async () => {
    // Navigate to mock payment page; payment happens on click there
    navigate(`/payment/mock`);
  };

  return { startCheckout };
}
