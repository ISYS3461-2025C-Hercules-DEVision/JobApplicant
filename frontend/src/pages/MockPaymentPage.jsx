import { useSearchParams, useNavigate } from "react-router-dom";
import { subscriptionService } from "../modules/subscription/services/subscriptionService";

function MockPaymentPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();

  const paymentId = params.get("paymentId");

  const handlePay = async () => {
    await subscriptionService.confirmMockPayment(paymentId);

    // go back to subscription page
    navigate("/subscription");
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="bg-white p-10 border-4 border-black text-center">
        <h1 className="text-4xl font-black mb-6">
          Mock Payment
        </h1>

        <p className="mb-6 font-medium">
          Payment ID: {paymentId}
        </p>

        <button
          onClick={handlePay}
          className="px-8 py-4 bg-black text-white font-black hover:opacity-80"
        >
          PAY $10
        </button>
      </div>
    </div>
  );
}

export default MockPaymentPage;
