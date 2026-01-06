import {useState, useEffect} from "react";
import {searchProfileService} from "../services/searchProfileService.js";

export const useSearchProfile = (applicantId) => {
    const [profiles, setProfiles]  = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if(!applicantId) return;

        const fetchProfile = async () => {
            try{
                const data = await searchProfileService.getAll(applicantId);
                setProfiles(data);
            }catch (err){
                setError(err.message);
            }finally {
                setLoading(false);
            }
        };
        fetchProfile();
    }, [applicantId]);

    const createProfile = async (data) => {
        const newProfile = await searchProfileService.create(data);
        setProfiles([...profiles, newProfile])
        return newProfile;
    };

    const updateProfile = async (id, data) => {
        const updated = await searchProfileService.update(id, data);
        setProfiles(profiles.map(p => p.searchProfileId === id ? updated : p));
        return updated;
    }

    const deleteProfile = async (id) => {
        await searchProfileService.delete(id);
        setProfiles(profiles.filter(p => p.searchProfileId !== id));
    };

    return {profiles, loading, error, createProfile, updateProfile, deleteProfile}
}