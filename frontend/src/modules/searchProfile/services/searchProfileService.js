// frontend/src/modules/searchProfile/services/searchProfileService.js
import { request } from "../../../utils/HttpUtil.js";

export const searchProfileService = {
  get(applicantId) {
    return request(`/api/v1/subscriptions/${applicantId}/search-profile`, {
      method: "GET",
    });
  },

  upsert(applicantId, payload) {
    return request(`/api/v1/subscriptions/${applicantId}/search-profile`, {
      method: "POST",
      body: payload,
    });
  },
};
