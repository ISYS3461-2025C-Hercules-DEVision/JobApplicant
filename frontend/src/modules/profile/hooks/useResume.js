// src/modules/profile/hooks/useResume.js
import { useState, useEffect } from "react";
import { resumeService } from "../services/resumeService";

export const useResume = (applicantId) => {
    const [resume, setResume] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!applicantId) {
            setLoading(false);
            return;
        }

        const fetchResume = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await resumeService.getResumeByApplicant(applicantId);
                console.log("✅ RESUME FETCHED:", data);
                setResume(data || null);
            } catch (err) {
                setError(err.message || "Failed to load resume");
                setResume(null);
            } finally {
                setLoading(false);
            }
        };

        fetchResume();
    }, [applicantId]);

    const updateResume = async (payload) => {
        if (!applicantId) throw new Error("Applicant ID is required");

        setIsSubmitting(true);
        setError(null);

        try {
            const updated = await resumeService.updateResume(applicantId, payload);
            setResume(updated);
            return updated;
        } catch (err) {
            setError(err.message || "Failed to update resume");
            throw err;
        } finally {
            setIsSubmitting(false);
        }
    };

    const deleteResume = async () => {
        if (!applicantId) throw new Error("Applicant ID is required");

        setIsSubmitting(true);
        setError(null);

        try {
            await resumeService.deleteResume(applicantId);
            setResume(null);
        } catch (err) {
            setError(err.message || "Failed to delete resume");
            throw err;
        } finally {
            setIsSubmitting(false);
        }
    };

    return {
        resume,
        loading,
        error,
        isSubmitting,
        updateResume,
        deleteResume,
    };
};