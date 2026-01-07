import {useState, useEffect} from "react";
import {profileService} from "../services/profileService.js";

export const useProfile = (applicantId) => {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    console.log('Fetching for ID: ', applicantId);

    useEffect(() => {
        // Reset state when applicantId changes
        setProfile(null);
        setError(null);
        setLoading(true);

        if (!applicantId) {
            setLoading(false); // no ID → not loading
            return;
        }

        const fetchProfile = async () => {
            try {
                const data = await profileService.getProfile(applicantId);
                setProfile(data);
            } catch (err) {
                console.error("Profile fetch error:", err);
                setError(err);
            } finally {
                setLoading(false);
            }
        };

        fetchProfile();
    }, [applicantId]);

    // Function to update profile
    const updateProfile = async (updateData) => {
        try {
            const updated = await profileService.updateProfile(applicantId, updateData);
            setProfile(updated); // Optimistic update
            return updated;
        } catch (err) {
            setError(err);
            throw err;
        }
    };

    //Delete profile by field
    const deleteField = async (fieldName) => {
        try{
            const updated = await profileService.deleteProfileByField(applicantId, fieldName);
            setProfile(updated);
            return updated;
        }catch (err) {
            setError(err);
            throw err;
        }
    }

    return {
        profile,
        loading,
        error,
        updateProfile,
        deleteField,
    };
}