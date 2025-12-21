import {useEffect, useState} from "react";
import {profileService} from "../services/profileService.js";

export const useEducation = (applicantId) => {
    const [educations, setEducations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!applicantId) return;
        (async () => {
            try {
                const data = await profileService.getProfile(applicantId);
                setEducations(data.educations || []);
            } catch (err) {
                setError(err);
            } finally {
                setLoading(false);
            }
        })();
    }, [applicantId]);

    const updateEducations = async (newEducations) => {
        try{
            await profileService.updateProfile(applicantId, {educations: newEducations});
            setEducations(newEducations);
        } catch (err){
            setError(err);
        }
    };

    return{educations, loading, error, updateEducations};
}