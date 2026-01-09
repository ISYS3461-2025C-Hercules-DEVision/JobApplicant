import { useCallback, useEffect, useMemo, useState } from "react";
import { notificationService } from "../services/notificationService.js";

function storageKey(applicantId) {
  return `notifications:lastSeenAt:${applicantId}`;
}

export function useNotifications(applicantId) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const lastSeenAt = useMemo(() => {
    if (!applicantId) return 0;
    const raw = localStorage.getItem(storageKey(applicantId));
    return raw ? Number(raw) : 0;
  }, [applicantId]);

  const newCount = useMemo(() => {
    if (!Array.isArray(items) || !lastSeenAt) {
      // if lastSeenAt is 0 (never opened), count all items as new
      return Array.isArray(items) ? items.length : 0;
    }
    return items.filter((n) => {
      const ts = n?.matchedAt ? Date.parse(n.matchedAt) : 0;
      return ts > lastSeenAt;
    }).length;
  }, [items, lastSeenAt]);

  const refresh = useCallback(async () => {
    if (!applicantId) return;
    setLoading(true);
    setError(null);
    try {
      const list = await notificationService.list(applicantId);
      setItems(Array.isArray(list) ? list : []);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  }, [applicantId]);

  const markAllSeen = useCallback(() => {
    if (!applicantId) return;
    localStorage.setItem(storageKey(applicantId), String(Date.now()));
  }, [applicantId]);

  // Lazy: no automatic fetching; caller triggers refresh when needed (e.g., on open)

  return { items, loading, error, newCount, refresh, markAllSeen };
}
