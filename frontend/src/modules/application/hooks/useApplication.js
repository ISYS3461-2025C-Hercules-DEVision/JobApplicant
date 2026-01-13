import { useCallback, useEffect, useState } from "react";
import { getApplicationsByApplicantId } from "../services/applicationService";

export function useApplicantApplications(applicantId) {
    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const fetchApplications = useCallback(async () => {
        if (!applicantId) {
            setApplications([]);
            setLoading(false);
            setError("Missing applicantId");
            return;
        }

        setLoading(true);
        setError("");

        try {
            const res = await getApplicationsByApplicantId(applicantId);


            const apps = Array.isArray(res) ? res : Array.isArray(res?.data) ? res.data : [];

            setApplications(apps);
        } catch (err) {
            console.error("❌ Failed to load applications", err);
            setApplications([]);
            setError(err?.message || "Failed to load applications");
        } finally {
            setLoading(false);
        }
    }, [applicantId]);

    useEffect(() => {
        fetchApplications();
    }, [fetchApplications]);

    return {
        applications,
        loading,
        error,
        refetch: fetchApplications,
    };
}
