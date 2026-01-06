// frontend/src/modules/subscription/services/subscriptionService.js
import { request } from "../../../utils/HttpUtil.js";

export const subscriptionService = {
  getMySubscription() {
    return request("/api/subscriptions/me");
  },

  createCheckoutSession() {
    return request("/api/subscriptions/checkout", {
      method: "POST",
    });
  },

  confirmMockPayment() {
    return request("/api/subscriptions/mock/confirm", {
      method: "POST",
    });
  },
};
