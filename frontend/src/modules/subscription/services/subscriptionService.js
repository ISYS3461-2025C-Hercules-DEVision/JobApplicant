import { request } from "../../../utils/HttpUtil.js";

export const subscriptionService = {
    /**
     * Get current applicant subscription
     */
    getMySubscription() {
        return request("/api/subscriptions/me", {
            headers: {
                "X-Applicant-Id": "test-user-1", // TEMP (until auth wired)
            },
        });
    },

    /**
     * Start checkout (Simplex)
     */
    createCheckoutSession() {
        return request("/api/subscriptions/checkout", {
            method: "POST",
            headers: {
                "X-Applicant-Id": "test-user-1",
                "X-Applicant-Email": "test-user-1@email.com",
            },
        });
    },
};
