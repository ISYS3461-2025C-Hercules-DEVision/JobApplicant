import {useState, useEffect} from "react";
import {profileService} from "../services/profileService.js";

export const useExperience = (applicantId) => {
    const [experiences, setExperiences] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!applicantId) return;
        (async () => {
            try {
                const data = await profileService.getProfile(applicantId);
                setExperiences(data.experiences || []);
            } catch (err) {
                setError(err);
            } finally {
                setLoading(false);
            }
        })();
    },[applicantId]);

    const updateExperiences = async (newExperiences) => {
        try {
            await profileService.updateProfile(applicantId, {experiences: newExperiences});
            setExperiences(newExperiences);
        } catch (err){
            setError(err);
        }
    };

    return{experiences, loading, error, updateExperiences};
}