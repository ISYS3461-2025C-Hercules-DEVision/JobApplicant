import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { subscriptionService } from "../modules/subscription/services/subscriptionService";
import { emitSubscriptionUpdated } from "../modules/subscription/events/subscriptionEvents";

export default function SubscriptionReturnPage() {
  const navigate = useNavigate();
  const { search } = useLocation();

  useEffect(() => {
    const params = new URLSearchParams(search);
    const sessionId = params.get("session_id");
    if (!sessionId) {
      navigate("/subscription", { replace: true });
      return;
    }

    (async () => {
      try {
        await subscriptionService.completePayment(sessionId);
        emitSubscriptionUpdated();
      } catch (e) {
        console.error("Complete payment failed", e);
      } finally {
        navigate("/subscription", { replace: true });
      }
    })();
  }, [search, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <h1 className="text-3xl font-black mb-2">Finalizing your payment...</h1>
        <p className="text-gray-600">Please wait a moment.</p>
      </div>
    </div>
  );
}
