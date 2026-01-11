import {useState, useEffect} from "react";
import {resumeService} from "../services/resumeService.js";

export const useMediaPortfolio = (resumeId) => {
    const [mediaItems, setMediaItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [uploading, setUploading] = useState(false);

    //Fetch portfolio
    useEffect(() => {
        // If no resumeId yet → keep loading, don't error yet
        if (resumeId === undefined || resumeId === null) {
            return; // wait for resumeId to arrive
        }

        const fetchMedia = async () => {
            setLoading(true);
            setError(null);

            try {
                const data = await resumeService.getMediaPortfolio(resumeId);
                setMediaItems(Array.isArray(data) ? data : []);
            } catch (err) {
                console.error("Media portfolio fetch error:", err);
                setError(err.message || "Failed to load media portfolio");
                setMediaItems([]);
            } finally {
                setLoading(false);
            }
        };

        fetchMedia();
    }, [resumeId]);

    //Upload new media item
    const uploadMedia = async (file, title = "", description = "", visibility = "PRIVATE") => {
        setUploading(true);
        try {
            const newMedia = await resumeService.uploadMediaPortfolio(
                resumeId,
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

    const deleteMedia = async (mediaId) => {
        try{
            await resumeService.deleteMedia(resumeId, mediaId);
            setMediaItems(mediaItems.filter(item => item.mediaId !== mediaId));
        } catch (err) {
            setError(err);
            throw err;

        }
    }
    return {mediaItems, loading, error, uploading, uploadMedia, deleteMedia};
}