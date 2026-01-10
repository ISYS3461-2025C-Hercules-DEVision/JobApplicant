// src/hooks/useJobs.js
import { useCallback, useEffect, useMemo, useState } from "react";
import { getJobs } from "../services/jobService";

/**
 * Expected backend response: Spring Page<JobPost>
 * {
 *   "content": [...],
 *   "totalPages": 5,
 *   "totalElements": 45,
 *   "number": 0, // (0-based from Spring)
 *   "size": 10,
 *   ...
 * }
 */
export function useJobs(initial = {}) {
    const [filters, setFilters] = useState({
        title: initial.title || "",
        location: initial.location || "",
        employmentType: initial.employmentType || "",
        keyWord: initial.keyWord || "",
    });

    const [page, setPage] = useState(initial.page || 1); // 1-based for your API
    const [size, setSize] = useState(initial.size || 10);

    const [data, setData] = useState({
        content: [],
        totalPages: 0,
        totalElements: 0,
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const fetchJobs = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            const res = await getJobs({
                ...filters,
                page,
                size,
            });

            // res is Spring Page
            setData({
                content: res?.content || [],
                totalPages: res?.totalPages ?? 0,
                totalElements: res?.totalElements ?? 0,
                number: res?.number ?? 0,
                size: res?.size ?? size,
            });
        } catch (err) {
            setError(err?.message || "Failed to load jobs");
        } finally {
            setLoading(false);
        }
    }, [filters, page, size]);

    // auto fetch whenever filters/page/size change
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
                setFilters({
                    title: "",
                    location: "",
                    employmentType: "",
                    keyWord: "",
                });
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
