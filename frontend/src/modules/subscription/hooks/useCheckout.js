import { subscriptionService } from "../services/subscriptionService";

export function useCheckout() {
  const startCheckout = async () => {
    const res = await subscriptionService.createCheckoutSession();
    window.location.href = res.data.checkoutUrl;
  };

  return { startCheckout };
}
