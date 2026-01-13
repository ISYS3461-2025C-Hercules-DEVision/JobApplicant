// frontend/src/modules/subscription/services/subscriptionService.js
import { request } from "../../../utils/HttpUtil.js";

export const subscriptionService = {

  // GET current subscription by applicantId
  getMySubscription(applicantId) {
    return request(`/api/v1/subscriptions/${applicantId}`, {
      method: "GET",
    });
  },

  // Create mock payment
  createCheckoutSession(applicantId, email) {
    const qp = email ? `?email=${encodeURIComponent(email)}` : "";
    return request(`/api/v1/subscriptions/${applicantId}/checkout${qp}`, {
      method: "POST",
    });
  },

  // (later) confirm mock payment if needed
  confirmMockPayment(paymentId) {
    return request(`/api/v1/subscriptions/mock/confirm?paymentId=${paymentId}`, {
      method: "POST",
    });
  },

  // Complete Stripe payment by sessionId
  completePayment(sessionId) {
    return request(`/api/v1/subscriptions/complete?sessionId=${encodeURIComponent(sessionId)}`, {
      method: "GET",
    });
  },
};
