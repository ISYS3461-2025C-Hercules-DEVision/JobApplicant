// JobListPanel.jsx
import React from "react";
import { useJobs } from "../hooks/useJobs";

// Small helper: safe field fallback
function pick(obj, keys, fallback = "") {
    for (const k of keys) {
        const v = obj?.[k];
        if (v !== undefined && v !== null && String(v).trim() !== "") return v;
    }
    return fallback;
}

function JobListPanel({ onSelectJob, selectedJob }) {
    const {
        jobs,
        loading,
        error,
        usingMock,          // ✅ use this instead of info
        page,
        totalPages,
        setPage,
        filters,
        setFilters,
    } = useJobs({ page: 1, size: 10 });

    return (
        <div className="space-y-4">
            {/* Filters */}
            <div className="border-4 border-black p-4 bg-white shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]">
                <p className="font-black uppercase mb-3">Search Jobs</p>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                    <input
                        value={filters.title}
                        onChange={(e) => setFilters((prev) => ({ ...prev, title: e.target.value }))}
                        placeholder="TITLE"
                        className="w-full px-3 py-2 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                    />

                    <input
                        value={filters.location}
                        onChange={(e) => setFilters((prev) => ({ ...prev, location: e.target.value }))}
                        placeholder="LOCATION"
                        className="w-full px-3 py-2 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                    />

                    <select
                        value={filters.employmentType}
                        onChange={(e) =>
                            setFilters((prev) => ({ ...prev, employmentType: e.target.value }))
                        }
                        className="w-full px-3 py-2 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                    >
                        <option value="">EMPLOYMENT TYPE</option>
                        <option value="FULL_TIME">FULL_TIME</option>
                        <option value="PART_TIME">PART_TIME</option>
                        <option value="INTERN">INTERN</option>
                        <option value="CONTRACT">CONTRACT</option>
                    </select>

                    <input
                        value={filters.keyWord}
                        onChange={(e) => setFilters((prev) => ({ ...prev, keyWord: e.target.value }))}
                        placeholder="KEYWORD"
                        className="w-full px-3 py-2 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                    />
                </div>

                <p className="text-xs text-gray-600 font-semibold mt-3">
                    Results auto-update when filters change.
                </p>
            </div>

            {/* Loading */}
            {loading && (
                <div className="border-4 border-black p-4 bg-white font-black uppercase">
                    Loading jobs...
                </div>
            )}

            {/* Error */}
            {error && (
                <div className="border-4 border-red-600 p-4 bg-red-50 text-red-800 font-black">
                    {error}
                </div>
            )}

            {/* ✅ Mock info banner */}
            {!loading && usingMock && (
                <div className="border-4 border-yellow-600 p-4 bg-yellow-50 text-yellow-900 font-black">
                    API failed — showing mock jobs.
                </div>
            )}

            {/* Empty */}
            {!loading && !error && jobs.length === 0 && (
                <div className="border-4 border-black p-4 bg-white font-black uppercase">
                    No jobs found.
                </div>
            )}

            {/* List */}
            {!loading &&
                !error &&
                jobs.map((job) => {
                    const jobId = pick(job, ["id", "jobId", "jobPostId"], "");
                    const isSelected =
                        selectedJob?.id === jobId || selectedJob?.jobId === jobId;

                    const title = pick(job, ["title", "jobTitle"], "Untitled Job");
                    const company = pick(
                        job,
                        ["companyName", "company", "employerName"],
                        "Unknown Company"
                    );
                    const location = pick(job, ["location", "jobLocation"], "Unknown Location");

                    const skillsRaw = job?.skills;
                    const skills = Array.isArray(skillsRaw) ? skillsRaw.join(", ") : skillsRaw || "";

                    const createdAt = pick(job, ["createdAt", "postedAt"], "");
                    const expiresIn = pick(job, ["expiresIn", "expiredInDays", "expires"], "");

                    return (
                        <div
                            key={jobId || JSON.stringify(job)}
                            onClick={() => onSelectJob(job)}
                            className={`
                cursor-pointer border-4 border-black p-4 
                shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]
                hover:translate-x-1 hover:translate-y-1 hover:shadow-none
                transition-none
                ${isSelected ? "bg-primary text-white" : "bg-white text-black"}
              `}
                        >
                            <h3 className="text-lg font-black">{title}</h3>
                            <p className="font-bold">{company}</p>
                            <p className="text-sm">{location}</p>

                            {skills && <p className="text-sm">Skills: {skills}</p>}

                            <div className="flex justify-between mt-2 text-sm font-bold">
                                <span>{createdAt ? `Posted: ${createdAt}` : "Posted: -"}</span>
                                <span className={isSelected ? "text-black" : "text-primary"}>
                  {expiresIn ? `Expires in ${expiresIn}` : "Expires: -"}
                </span>
                            </div>
                        </div>
                    );
                })}

            {/* Pagination */}
            {!loading && !error && totalPages > 1 && (
                <div className="flex items-center justify-between border-4 border-black p-4 bg-white shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]">
                    <button
                        type="button"
                        disabled={page <= 1}
                        onClick={() => setPage((p) => Math.max(1, p - 1))}
                        className="bg-white text-black font-black py-2 px-4 border-4 border-black uppercase hover:bg-black hover:text-white disabled:opacity-50"
                    >
                        Prev
                    </button>

                    <p className="font-black uppercase">
                        Page {page} / {totalPages}
                    </p>

                    <button
                        type="button"
                        disabled={page >= totalPages}
                        onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
                        className="bg-white text-black font-black py-2 px-4 border-4 border-black uppercase hover:bg-black hover:text-white disabled:opacity-50"
                    >
                        Next
                    </button>
                </div>
            )}
        </div>
    );
}

export default JobListPanel;
