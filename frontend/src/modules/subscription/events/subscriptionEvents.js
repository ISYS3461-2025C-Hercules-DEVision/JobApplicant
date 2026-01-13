// frontend/src/modules/subscription/events/subscriptionEvents.js

const EVENT_NAME = "subscription-updated";

export function emitSubscriptionUpdated() {
  try {
    window.dispatchEvent(new CustomEvent(EVENT_NAME));
  } catch (_) {
    // ignore
  }
}

export function onSubscriptionUpdated(handler) {
  window.addEventListener(EVENT_NAME, handler);
}

export function offSubscriptionUpdated(handler) {
  window.removeEventListener(EVENT_NAME, handler);
}
