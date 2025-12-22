import Modal from "../../../components/Modal/Modal";

function PaymentModal({ email, onSuccess, onClose }) {
  return (
    <Modal title="Complete Subscription Payment" onClose={onClose}>
      <p className="font-bold mb-4">
        You will be charged <strong>$10/month</strong>.
      </p>

      <p className="text-sm font-bold mb-6">
        Subscriber email: {email}
      </p>

      <button
        onClick={onSuccess}
        className="
          w-full bg-primary text-white font-black py-3
          border-4 border-black mb-3
          hover:translate-x-1 hover:translate-y-1 hover:shadow-none
          shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]
          transition-none
        "
      >
        Pay with Stripe
      </button>

      <button
        onClick={onSuccess}
        className="
          w-full bg-white text-black font-black py-3
          border-4 border-black
          hover:bg-black hover:text-white
          transition-none
        "
      >
        Pay with PayPal
      </button>
    </Modal>
  );
}

export default PaymentModal;
