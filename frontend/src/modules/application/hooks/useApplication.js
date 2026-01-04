import { useEffect, useState } from "react";
import { listMyApplications } from "../services/applicationService";

export default function useApplication() {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      setLoading(true);
      const res = await listMyApplications();
      if (res.status >= 200 && res.status < 300) setApplications(res.json);
      setLoading(false);
    })();
  }, []);

  return { applications, loading };
}
