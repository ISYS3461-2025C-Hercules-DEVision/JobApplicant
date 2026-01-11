import { useState, useEffect } from "react";
import { resumeService } from "../services/resumeService";


export const useResume = (applicantId) => {
    const [resume, setResume] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        // Reset state when applicantId changes
        setResume(null);
        setError(null);
        setLoading(true);

        if (!applicantId) {
            setLoading(false);
            return;
        }

        const fetchResume = async () => {
            try {
                const data = await resumeService.getResumeByApplicant(applicantId);
                console.log("Resume fetched:", data);
                setResume(data);
            } catch (err) {
                console.error("Profile fetch error:", err);
                setError(err);
            } finally {
                setLoading(false);
            }
        };

        fetchResume();
    }, [applicantId]);

    const updateResume = async (updateData) => {
        try {
            const updated = await resumeService.updateResume(applicantId, updateData);
            console.log("Resume updated:", updated);
            setResume(updated); // Optimistic update
            return updated;
        } catch (err) {
            setError(err);
            throw err;
        }
    };

    // const deleteResume = async (fieldName) => {
    //     try{
    //         const updated = await resumeService.deleteResumeByField(applicantId, fieldName);
    //         setResume(updated);
    //         return updated;
    //     }catch (err) {
    //         setError(err);
    //         throw err;
    //     }
    // }

    return {
        resume,
        loading,
        error,
        updateResume
    };
};