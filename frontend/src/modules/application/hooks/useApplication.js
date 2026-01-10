import { useCallback, useEffect, useState } from "react";
import { getApplicationsByApplicantId } from "../services/applicationService";

export function useApplicantApplications(applicantId) {
    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const fetchApplications = useCallback(async () => {
        if (!applicantId) return;

        setLoading(true);
        setError("");

        try {
            const res = await getApplicationsByApplicantId(applicantId);
            setApplications(Array.isArray(res) ? res : []);
        } catch (err) {
            setError(err?.message || "Failed to load applications.");
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
