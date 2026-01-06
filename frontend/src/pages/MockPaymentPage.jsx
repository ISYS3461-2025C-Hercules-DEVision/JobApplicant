import { subscriptionService } from "../modules/subscription/services/subscriptionService";
import { useNavigate } from "react-router-dom";

function MockPaymentPage() {
  const navigate = useNavigate();

  const handlePay = async () => {
    await subscriptionService.confirmMockPayment();
    navigate("/profile");
  };

  return (
    <div className="min-h-screen flex items-center justify-center">
      <button
        onClick={handlePay}
        className="px-10 py-4 bg-black text-white font-black text-xl"
      >
        PAY (MOCK)
      </button>
    </div>
  );
}

export default MockPaymentPage;
