import {useState} from "react";
import {profileService} from "../services/profileService.js";

export const useAvatarUpload = (applicantId) => {
    const [uploading, setUploading] = useState(false);
    const [error, setError] = useState(null);

    const uploadAvatar = async (file) => {
        setUploading(true);
        try{
            const updated = await profileService.uploadAvatar(applicantId, file);
            return updated.profileImageUrl; //return new URL
        }catch(err){
            setError(err);
            return null;
        }finally {
            setUploading(false);
        }
    };

    return {uploadAvatar, uploading, error};
};