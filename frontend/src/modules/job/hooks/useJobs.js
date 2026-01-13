import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { getJobs } from "../services/jobService";
import { getCompanyPublicProfile } from "../services/companyServices.js";
import { mockJobsPage } from "../ui/mockData.js"; // adjust path

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
    const [usingMock, setUsingMock] = useState(false);

    const companyCacheRef = useRef(new Map());

    // ✅ never throw from company enrichment
    const enrichJobsWithCompany = useCallback(async (jobs) => {
        const companyIds = Array.from(new Set(jobs.map((j) => j.companyId).filter(Boolean)));
        const missing = companyIds.filter((id) => !companyCacheRef.current.has(id));

        if (missing.length) {
            await Promise.all(
                missing.map(async (id) => {
                    try {
                        const profile = await getCompanyPublicProfile(id);
                        companyCacheRef.current.set(id, profile);
                    } catch {
                        // ✅ fallback so UI still works
                        companyCacheRef.current.set(id, { companyId: id, displayName: "Unknown Company" });
                    }
                })
            );
        }

        return jobs.map((job) => {
            const profile = companyCacheRef.current.get(job.companyId);
            return {
                ...job,
                companyName: job.companyName || profile?.displayName || "Unknown Company",
                companyProfile: profile,
            };
        });
    }, []);

    const loadFromMock = useCallback(async (reason = "API failed") => {
        const enrichedMock = await enrichJobsWithCompany(mockJobsPage.content);

        setData({
            content: enrichedMock,
            totalPages: mockJobsPage.totalPages ?? 1,
            totalElements: mockJobsPage.totalElements ?? enrichedMock.length,
            number: mockJobsPage.number ?? 0,
            size: mockJobsPage.size ?? size,
        });

        setUsingMock(true);

        // ✅ IMPORTANT: do not treat fallback as an "error" for UI
        setError("");
    }, [enrichJobsWithCompany, size]);

    const fetchJobs = useCallback(async () => {
        setLoading(true);
        setError("");

        try {
            // 1) Fetch jobs
            const res = await getJobs({ ...filters, page, size });
            const jobs = res?.content || [];

            // 2) Enrich jobs (company profile failures won't crash)
            const enriched = await enrichJobsWithCompany(jobs);

            setData({
                content: enriched,
                totalPages: res?.totalPages ?? 0,
                totalElements: res?.totalElements ?? 0,
                number: res?.number ?? 0,
                size: res?.size ?? size,
            });

            setUsingMock(false);
        } catch (err) {
            // ✅ Any failure here -> mock
            await loadFromMock(err?.message || "Failed to fetch jobs");
        } finally {
            setLoading(false);
        }
    }, [filters, page, size, enrichJobsWithCompany, loadFromMock]);

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
        usingMock,
        filters,
        ...actions,
    };
}
