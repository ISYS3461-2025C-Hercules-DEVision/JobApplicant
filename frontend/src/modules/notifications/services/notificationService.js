import { request } from "../../../utils/HttpUtil.js";

export const notificationService = {
  list(applicantId) {
    return request(`/api/v1/subscriptions/notifications/${applicantId}`, {
      method: "GET",
    });
  },
};
