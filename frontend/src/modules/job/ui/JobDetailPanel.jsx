import React, { useRef, useState } from "react";
import { applyToJob } from "../services/ApplyJobFlowService.js";

function JobDetailPanel({ job }) {
    const fileRef = useRef(null);

    const [showApplyModal, setShowApplyModal] = useState(false);
    const [loading, setLoading] = useState(false);
    const [successMsg, setSuccessMsg] = useState("");
    const [errorMsg, setErrorMsg] = useState("");

    if (!job) {
        return (
            <div className="bg-white border-4 border-black p-10 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] text-center font-black text-xl">
                Select a job from the list to view details
            </div>
        );
    }

    // Fallbacks
    const title = job.title || job.jobTitle || "Untitled Job";
    const company = job.company || job.companyName || job.employerName || "Unknown Company";
    const location = job.location || job.jobLocation || "Unknown Location";

    const salary = job.salaryRange || job.salary || "-";
    const workMode = job.workMode || "-";
    const experience = job.experienceLevel || job.level || "-";
    const postedAt = job.postedAt || job.createdAt || "-";
    const expiresIn = job.expiresIn || "-";
    const applicants = job.applicantsCount ?? 0;

    const skills = Array.isArray(job.skills) ? job.skills : [];
    const responsibilities = Array.isArray(job.responsibilities) ? job.responsibilities : [];
    const requirements = Array.isArray(job.requirements) ? job.requirements : [];
    const benefits = Array.isArray(job.benefits) ? job.benefits : [];

    // ✅ Open modal on apply
    const handleApplyClick = () => {
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        const applicantId = user?.applicantId;

        if (!applicantId) {
            alert("⚠️ Please login first before applying.");
            return;
        }

        setSuccessMsg("");
        setErrorMsg("");
        setShowApplyModal(true);
    };

    // ✅ Trigger file picker
    const handleUploadClick = () => {
        fileRef.current?.click();
    };

    // ✅ After file selected -> upload + save application
    const handleFileChange = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;

        // reset input so user can re-upload same file if needed
        e.target.value = "";

        const user = JSON.parse(localStorage.getItem("user") || "{}");
        const applicantId = user?.applicantId;

        setErrorMsg("");
        setSuccessMsg("");

        try {
            setLoading(true);

            await applyToJob({ job, file, applicantId });

            // ✅ Show success inside modal
            setSuccessMsg("✅ CV uploaded & application submitted successfully!");
            setErrorMsg("");

            // ✅ Close modal after 1.2s (optional)
            setTimeout(() => {
                setShowApplyModal(false);
            }, 1200);
        } catch (err) {
            console.error(err);
            setErrorMsg(err?.message || "Failed to upload or submit application.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="bg-white border-4 border-black shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] h-[80vh] flex flex-col relative">

            {/* ✅ Apply Modal */}
            {showApplyModal && (
                <div className="absolute inset-0 z-50 flex items-center justify-center">
                    {/* overlay */}
                    <div
                        className="absolute inset-0 bg-black/40"
                        onClick={() => !loading && setShowApplyModal(false)}
                    />

                    {/* modal */}
                    <div className="relative bg-white border-4 border-black p-8 w-[90%] max-w-md shadow-[8px_8px_0px_0px_rgba(0,0,0,1)]">
                        <h2 className="text-2xl font-black uppercase">Upload your CV</h2>
                        <p className="font-bold mt-2 text-sm text-gray-700">
                            Please upload your CV in <span className="text-black">PDF</span> format to apply for this job.
                        </p>

                        {/* job preview */}
                        <div className="mt-4 border-4 border-black p-4 bg-gray-50">
                            <p className="font-black">{title}</p>
                            <p className="font-bold text-sm">{company}</p>
                            <p className="text-xs text-gray-700">{location}</p>
                        </div>

                        {/* messages */}
                        {errorMsg && (
                            <div className="mt-4 border-4 border-red-600 bg-red-50 p-3 text-red-700 font-black text-sm">
                                {errorMsg}
                            </div>
                        )}

                        {successMsg && (
                            <div className="mt-4 border-4 border-green-700 bg-green-50 p-3 text-green-800 font-black text-sm">
                                {successMsg}
                            </div>
                        )}

                        {/* hidden file input */}
                        <input
                            ref={fileRef}
                            type="file"
                            accept="application/pdf"
                            hidden
                            onChange={handleFileChange}
                        />

                        {/* actions */}
                        <div className="flex gap-3 mt-6">
                            <button
                                type="button"
                                disabled={loading}
                                onClick={() => setShowApplyModal(false)}
                                className="w-1/2 px-4 py-3 border-4 border-black font-black uppercase hover:bg-black hover:text-white transition-none disabled:opacity-50"
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                disabled={loading}
                                onClick={handleUploadClick}
                                className="w-1/2 px-4 py-3 border-4 border-black font-black uppercase bg-primary text-white hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none disabled:opacity-50"
                            >
                                {loading ? "Uploading..." : "Upload here"}
                            </button>
                        </div>

                        {/* loading hint */}
                        {loading && (
                            <p className="mt-3 text-xs font-bold text-gray-700">
                                Uploading CV & saving application… please wait.
                            </p>
                        )}
                    </div>
                </div>
            )}

            {/* STATIC TOP SECTION */}
            <div className="p-8 border-b-4 border-black">
                <h1 className="text-3xl font-black">{title}</h1>
                <p className="font-bold mt-2">{company}</p>
                <p className="text-sm text-gray-700">{location}</p>

                <p className="text-sm mt-1 font-bold">
                    Posted {postedAt} ·{" "}
                    <span className="text-primary">{applicants} people clicked apply</span>
                </p>

                {/* Tags Row */}
                <div className="flex flex-wrap gap-2 mt-4">
          <span className="border-2 border-black px-3 py-1 font-black text-xs uppercase">
            {job.employmentType || "FULL_TIME"}
          </span>
                    <span className="border-2 border-black px-3 py-1 font-black text-xs uppercase">
            {workMode}
          </span>
                    <span className="border-2 border-black px-3 py-1 font-black text-xs uppercase">
            {experience}
          </span>
                    <span className="border-2 border-black px-3 py-1 font-black text-xs uppercase">
            Expires in {expiresIn}
          </span>
                    <span className="border-2 border-black px-3 py-1 font-black text-xs uppercase">
            Salary: {salary}
          </span>
                </div>

                {/* Buttons */}
                <div className="flex gap-4 mt-6">
                    <button className="px-6 py-3 border-4 border-black font-black hover:bg-black hover:text-white transition-none">
                        Save
                    </button>

                    {/* ✅ Apply now opens modal */}
                    <button
                        onClick={handleApplyClick}
                        className="px-6 py-3 border-4 border-black font-black bg-primary text-white hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none"
                    >
                        Apply →
                    </button>
                </div>
            </div>

            {/* SCROLLABLE CONTENT */}
            <div className="flex-1 overflow-y-auto p-8 space-y-8">
                {/* About */}
                <section>
                    <h2 className="text-xl font-black uppercase mb-3">About the Job</h2>
                    <p className="font-bold leading-relaxed">
                        {job.about || job.description || "No description provided."}
                    </p>
                </section>

                {/* Skills */}
                {skills.length > 0 && (
                    <section>
                        <h2 className="text-xl font-black uppercase mb-3">Skills</h2>
                        <div className="flex flex-wrap gap-2">
                            {skills.map((s) => (
                                <span
                                    key={s}
                                    className="border-4 border-black px-3 py-1 font-black uppercase text-xs bg-white shadow-[3px_3px_0px_0px_rgba(0,0,0,1)]"
                                >
                  {s}
                </span>
                            ))}
                        </div>
                    </section>
                )}

                {/* Responsibilities */}
                {responsibilities.length > 0 && (
                    <section>
                        <h2 className="text-xl font-black uppercase mb-3">Responsibilities</h2>
                        <ul className="list-disc ml-6 font-bold leading-relaxed space-y-2">
                            {responsibilities.map((r, idx) => (
                                <li key={idx}>{r}</li>
                            ))}
                        </ul>
                    </section>
                )}

                {/* Requirements */}
                {requirements.length > 0 && (
                    <section>
                        <h2 className="text-xl font-black uppercase mb-3">Requirements</h2>
                        <ul className="list-disc ml-6 font-bold leading-relaxed space-y-2">
                            {requirements.map((r, idx) => (
                                <li key={idx}>{r}</li>
                            ))}
                        </ul>
                    </section>
                )}

                {/* Benefits */}
                {benefits.length > 0 && (
                    <section>
                        <h2 className="text-xl font-black uppercase mb-3">Benefits</h2>
                        <ul className="list-disc ml-6 font-bold leading-relaxed space-y-2">
                            {benefits.map((b, idx) => (
                                <li key={idx}>{b}</li>
                            ))}
                        </ul>
                    </section>
                )}
            </div>
        </div>
    );
}

export default JobDetailPanel;
