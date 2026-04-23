// frontend/src/modules/subscription/services/subscriptionService.js // File location comment
import { request } from "../../../utils/HttpUtil.js"; // HTTP helper wrapper

export const subscriptionService = { // Subscription API client

  // GET current subscription by applicantId
  getMySubscription(applicantId) { // Fetch current subscription status
    return request(`/api/v1/subscriptions/${applicantId}`, { // API endpoint
      method: "GET", // HTTP GET
    }); // Return promise
  },

  // Create mock payment
  createCheckoutSession(applicantId, email) { // Start Stripe checkout session
    const qp = email ? `?email=${encodeURIComponent(email)}` : ""; // Optional email query param
    return request(`/api/v1/subscriptions/${applicantId}/checkout${qp}`, { // API endpoint
      method: "POST", // HTTP POST
    }); // Return promise
  },

  // (later) confirm mock payment if needed
  confirmMockPayment(paymentId) { // Legacy mock confirm (unused for Stripe)
    return request(`/api/v1/subscriptions/mock/confirm?paymentId=${paymentId}`, { // Mock endpoint
      method: "POST", // HTTP POST
    }); // Return promise
  },

  // Complete Stripe payment by sessionId
  completePayment(sessionId) { // Finalize checkout
    return request(`/api/v1/subscriptions/complete?sessionId=${encodeURIComponent(sessionId)}`, { // API endpoint
      method: "GET", // HTTP GET
    }); // Return promise
  },

  // Cancel current subscription and set plan to FREE
  cancelSubscription(applicantId) { // Cancel premium and downgrade to FREE
    return request(`/api/v1/subscriptions/${applicantId}/cancel`, { // API endpoint
      method: "POST", // HTTP POST
    }); // Return promise
  },
};
