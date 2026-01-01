import {useCallback, useEffect, useState} from "react";
import * as adminService from "../services/adminService.js";
export function useAdmin(){
    const [applicants, setApplicants] = useState([]);
    const [loadingApplicants, setLoadingApplicants] = useState(false);
    const [applicantsError, setApplicantsError] = useState(null);
    const [loadingToggleId, setLoadingToggleId] = useState(null);

    const fetchApplicants = useCallback(async ()=>{
        setLoadingApplicants (true);
        setApplicantsError(null);

        try{
            const data = await adminService.getAllApplicants();

            const list = Array.isArray(data) ? data : data?.data ?? [];

            setApplicants(list);
            return list;

        }catch (err){
            setApplicantsError(err?.messages || "Failed to fetch applicants");
            setApplicants([]);
            return [];
        } finally {
            setLoadingApplicants(false);
        }

    }, []);


    // Activate
    const activate = useCallback(async (id) => {
        setLoadingToggleId(id);

        // optimistic update
        setApplicants((prev) =>
            prev.map((a) => (a.id === id ? { ...a, status: true } : a))
        );

        try {
            await adminService.activateApplicant(id);
        } catch (err) {
            // rollback if API fails
            setApplicants((prev) =>
                prev.map((a) =>
                    prev.map((a) => (a.id === id ? { ...a, status: false } : a))
                )
            );
            alert(err?.message || "Activate failed");
        } finally {
            setLoadingToggleId(null);
        }
    }, []);

    // Deactivate
    const deactivate = useCallback(async (id) => {
        setLoadingToggleId(id);

        // optimistic update
        setApplicants((prev) =>
            prev.map((a) => (a.id === id ? { ...a, status: false } : a))
        );

        try {
            await adminService.deactivateApplicant(id);
        } catch (err) {
            // rollback if API fails
            setApplicants((prev) =>
                prev.map((a) =>
                    prev.map((a) => (a.id === id ? { ...a, status: true } : a))
                )
            );
            alert(err?.message || "Deactivate failed");
        } finally {
            setLoadingToggleId(null);
        }
    }, []);


    // auto loading
    useEffect(() => {
        fetchApplicants();
    }, [fetchApplicants]);

    return {
        applicants,
        loadingApplicants,
        applicantsError,
        fetchApplicants,
        activate,
        deactivate,
        loadingToggleId,
    };
}
