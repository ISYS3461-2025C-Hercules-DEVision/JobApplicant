import {useState, useEffect} from "react";
import {profileService} from "../services/profileService.js";

export const useMediaPortfolio = (applicantId) => {
    const [mediaItems, setMediaItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [uploading, setUploading] = useState(false);

    //Fetch portfolio
    useEffect(() => {
        if (!applicantId) return;
        (async () => {
            try {
                const data = await profileService.getMediaPortfolio(applicantId);
                setMediaItems(data);
            } catch (err) {
                setError(err);
            } finally {
                setLoading(false);
            }
        })();
    }, [applicantId]);

    //Upload new media item
    const uploadMedia = async (file, title = "", description = "", visibility = "PRIVATE") => {
        setUploading(true);
        try {
            const newMedia = await profileService.uploadMediaPortfolio(
                applicantId,
                file,
                title,
                description,
                visibility
            );
            setMediaItems([...mediaItems, newMedia]); //optimistic update
            return newMedia;
        } catch (err) {
            setError(err);
            throw err;
        } finally {
            setUploading(false);
        }
    };
    return {mediaItems, loading, error, uploading, uploadMedia};
}