import {useState, useEffect} from "react";
import {profileService} from "../services/profileService.js";

export const useSkills = (applicantId) => {
    const [skills, setSkils] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect (() =>{
        if(!applicantId) return;
        (async () => {
            try{
                const data = await profileService.getProfile(applicantId);
                setSkils(data.skills || []);
            }catch (err){
                setError(err);
            }finally {
                setLoading(false);
            }
        })();
    }, [applicantId]);

    const updateSkills = async (newSkills) => {
        try{
            await profileService.updateProfile(applicantId, {skills: newSkills});
            setSkils(newSkills);
        }catch (err){
            setError(err);
        }
    };

    return {skills, loading, error, updateSkills};
};