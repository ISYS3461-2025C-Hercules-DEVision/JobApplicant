import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { getJobs } from "../services/jobService";
import { getCompanyPublicProfile } from "../services/companyServices.js";

export function useJobs(initial = {}) {
    const [filters, setFilters] = useState({
        title: initial.title || "",
        location: initial.location || "",
        employmentType: initial.employmentType || "",
        keyWord: initial.keyWord || "",
    });

    const [page, setPage] = useState(initial.page || 1);
    const [size, setSize] = useState(initial.size || 10);

    const [data, setData] = useState({
        content: [],
        totalPages: 0,
        totalElements: 0,
        number: 0,
        size: initial.size || 10,
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    //  companyId -> {displayName, ...}
    const companyCacheRef = useRef(new Map());

    const fetchJobs = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const res = await getJobs({ ...filters, page, size });
            const jobs = res?.content || [];

            //  unique companyIds
            const companyIds = Array.from(
                new Set(jobs.map((j) => j.companyId).filter(Boolean))
            );

            //  fetch only missing company profiles
            const missing = companyIds.filter((id) => !companyCacheRef.current.has(id));

            if (missing.length) {
                await Promise.all(
                    missing.map(async (id) => {
                        try {
                            const profile = await getCompanyPublicProfile(id);
                            companyCacheRef.current.set(id, profile);
                        } catch (e) {
                            // cache fallback to avoid infinite retries
                            companyCacheRef.current.set(id, { companyId: id, displayName: "Unknown Company" });
                        }
                    })
                );
            }

            //  enrich each job
            const enriched = jobs.map((job) => {
                const profile = companyCacheRef.current.get(job.companyId);
                return {
                    ...job,
                    companyName: profile?.displayName || "Unknown Company",
                    companyProfile: profile, // optional if you want aboutUs/logoUrl later
                };
            });

            setData({
                content: enriched,
                totalPages: res?.totalPages ?? 0,
                totalElements: res?.totalElements ?? 0,
                number: res?.number ?? 0,
                size: res?.size ?? size,
            });
        } catch (err) {
            setData({
                content: [],
                totalPages: 0,
                totalElements: 0,
                number: 0,
                size,
            });
            setError(err?.message || "Failed to fetch jobs.");
        } finally {
            setLoading(false);
        }
    }, [filters, page, size]);

    useEffect(() => {
        fetchJobs();
    }, [fetchJobs]);

    const actions = useMemo(
        () => ({
            setFilters,
            setPage,
            setSize,
            refetch: fetchJobs,
            reset: () => {
                setFilters({ title: "", location: "", employmentType: "", keyWord: "" });
                setPage(1);
                setSize(10);
            },
        }),
        [fetchJobs]
    );

    return {
        jobs: data.content,
        page,
        size,
        totalPages: data.totalPages,
        totalElements: data.totalElements,
        loading,
        error,
        filters,
        ...actions,
    };
}
