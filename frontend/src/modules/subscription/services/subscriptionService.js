import http from "@/lib/http";

export const subscriptionService = {
  getMySubscription() {
    return http.get("/subscriptions/me");
  },

  createCheckoutSession() {
    return http.post("/subscriptions/checkout", {
      planType: "PREMIUM",
    });
  },
};
