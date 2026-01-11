import { useCallback, useEffect, useMemo, useState } from "react";
import { getJobs } from "../services/jobService";
import { mockJobsPage } from "../ui/mockData.js";

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
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [info, setInfo] = useState(""); // ✅ NEW

    const filterMockJobs = (mockContent) => {
        const t = filters.title.trim().toLowerCase();
        const loc = filters.location.trim().toLowerCase();
        const emp = filters.employmentType.trim();
        const kw = filters.keyWord.trim().toLowerCase();

        return mockContent.filter((job) => {
            const title = (job.title || "").toLowerCase();
            const location = (job.location || "").toLowerCase();
            const employmentType = job.employmentType || "";
            const company = (job.companyName || "").toLowerCase();
            const skills = Array.isArray(job.skills) ? job.skills.join(" ").toLowerCase() : "";

            const matchesTitle = !t || title.includes(t);
            const matchesLocation = !loc || location.includes(loc);
            const matchesEmploymentType = !emp || employmentType === emp;

            const matchesKeyword =
                !kw ||
                title.includes(kw) ||
                company.includes(kw) ||
                skills.includes(kw);

            return matchesTitle && matchesLocation && matchesEmploymentType && matchesKeyword;
        });
    };

    const fetchJobs = useCallback(async () => {
        setLoading(true);
        setError("");
        setInfo(""); // ✅ clear info too

        try {
            const res = await getJobs({
                ...filters,
                page,
                size,
            });

            setData({
                content: res?.content || [],
                totalPages: res?.totalPages ?? 0,
                totalElements: res?.totalElements ?? 0,
                number: res?.number ?? 0,
                size: res?.size ?? size,
            });
        } catch (err) {
            // ✅ Fallback to mock data
            const filtered = filterMockJobs(mockJobsPage.content);

            const start = (page - 1) * size;
            const end = start + size;
            const paged = filtered.slice(start, end);

            const totalElements = filtered.length;
            const totalPages = Math.max(1, Math.ceil(totalElements / size));

            setData({
                content: paged,
                totalPages,
                totalElements,
                number: page - 1,
                size,
            });

            // ✅ do NOT set error here (otherwise list won’t render)
            setInfo("API not available — showing mock data.");
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
        info, // ✅ return info
        filters,
        ...actions,
    };
}
