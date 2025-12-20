import {useState, useEffect} from "react";
import {profileService} from "../services/profileService.js";

export const useProfile = (applicantId) => {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    console.log('Fetching for ID: ', applicantId);

    useEffect(() => {

        (async () => {
            if(!applicantId) return;
            try {
                const data = await profileService.getProfile(applicantId);
                setProfile(data);
            } catch (err) {
                setError(err);
            } finally {
                setLoading(false);
            }
        })
        ();
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

    return {
        profile,
        loading,
        error,
        updateProfile,
    };
}