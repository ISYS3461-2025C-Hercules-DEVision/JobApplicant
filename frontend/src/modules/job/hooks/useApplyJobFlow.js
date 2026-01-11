import { useCallback, useRef, useState } from "react";
import { applyToJob } from "../services/ApplyJobFlowService.js";
export function useApplyJobFlow() {
    const fileRef = useRef(null);
    const [loading, setLoading] = useState(false);

    const getApplicantId = () => {
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        return user?.applicantId;
    };

    const handleApplyClick = useCallback((job) => {
        const applicantId = getApplicantId();
        if (!applicantId) {
            alert("⚠️ Please login first before applying.");
            return;
        }
        fileRef.current?.click();
    }, []);

    const handleFileChange = useCallback(async (e, job) => {
        const file = e.target.files?.[0];
        if (!file) return;
        e.target.value = "";

        const applicantId = getApplicantId();

        try {
            setLoading(true);

            const res = await applyToJob({ job, file, applicantId });

            alert("✅ CV uploaded successfully!");
            alert("🎉 Application submitted successfully!");

            console.log("Apply result:", res);
        } catch (err) {
            console.error(err);
            alert(err?.message || "Failed to apply.");
        } finally {
            setLoading(false);
        }
    }, []);

    return {
        fileRef,
        loading,
        handleApplyClick,
        handleFileChange,
    };
}