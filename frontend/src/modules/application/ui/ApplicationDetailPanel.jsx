import React, { useMemo } from "react";

function formatDate(iso) {
    if (!iso) return "-";
    try {
        return new Date(iso).toLocaleString();
    } catch {
        return iso;
    }
}

function buildTimeline(status) {
    if (!status) return ["Application Submitted"];
    if (status === "PENDING") return ["Application Submitted"];
    if (status === "VIEWED") return ["Application Submitted", "Viewed by Recruiter"];
    if (status === "ACCEPTED")
        return ["Application Submitted", "Viewed by Recruiter", "Accepted 🎉"];
    if (status === "REJECTED")
        return ["Application Submitted", "Viewed by Recruiter", "Rejected"];
    return ["Application Submitted", status];
}

// ✅ Cloudinary: force download mode (good for download, not for preview)
function toDownloadUrl(url) {
    if (!url) return url;
    if (url.includes("/upload/fl_attachment/")) return url;
    return url.replace("/upload/", "/upload/fl_attachment/");
}

/**
 * ✅ Open PDF in a new tab (PREVIEW) even if URL has no .pdf extension
 * - Fetch file as blob
 * - Force blob MIME type to application/pdf
 * - Open blob URL in a new tab
 */
async function openPdf(url) {
    if (!url) return;

    try {
        const res = await fetch(url, { method: "GET" });

        if (!res.ok) {
            throw new Error(`Open failed (${res.status})`);
        }

        const blob = await res.blob();

        // ✅ Force correct PDF MIME
        const pdfBlob =
            blob.type === "application/pdf"
                ? blob
                : new Blob([blob], { type: "application/pdf" });

        const blobUrl = window.URL.createObjectURL(pdfBlob);

        // ✅ open in new tab
        window.open(blobUrl, "_blank", "noopener,noreferrer");

        // ✅ give the tab time to load before revoking
        setTimeout(() => window.URL.revokeObjectURL(blobUrl), 60_000);
    } catch (err) {
        console.error("❌ openPdf failed:", err);
        alert(err?.message || "Failed to open PDF");
    }
}

/**
 * ✅ Force download as a REAL .pdf filename in browser
 * - Downloads the file via fetch
 * - Creates a blob URL
 * - Forces filename via <a download="">
 */
async function downloadPdf(url, filename = "cv.pdf") {
    if (!url) return;

    try {
        const res = await fetch(url, { method: "GET" });

        if (!res.ok) {
            throw new Error(`Download failed (${res.status})`);
        }

        const blob = await res.blob();

        const blobUrl = window.URL.createObjectURL(blob);

        const a = document.createElement("a");
        a.href = blobUrl;
        a.download = filename; // ✅ force .pdf filename
        document.body.appendChild(a);
        a.click();
        a.remove();

        window.URL.revokeObjectURL(blobUrl);
    } catch (err) {
        console.error("❌ downloadPdf failed:", err);
        alert(err?.message || "Failed to download PDF");
    }
}

function ApplicationDetailPanel({ application }) {
    const timeline = useMemo(
        () => buildTimeline(application?.status),
        [application?.status]
    );

    if (!application) {
        return (
            <div className="bg-white border-4 border-black p-10 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] text-center font-black text-xl">
                Select an application to view details
            </div>
        );
    }

    const jobTitle = application?.job?.title || "Unknown Job";
    const company = application?.job?.companyName || "Unknown Company";
    const location = application?.job?.location || "Unknown Location";

    const appliedDateText =
        application?.appliedDateText ||
        formatDate(application?.submissionDate || application?.createdAt);

    const docs = application?.documents || [];
    const status = application?.status || "PENDING";

    const statusLabel = {
        PENDING: "Submitted",
        VIEWED: "In Review",
        ACCEPTED: "Accepted",
        REJECTED: "Rejected",
    };

    return (
        <div className="bg-white border-4 border-black shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] h-[80vh] flex flex-col">
            {/* STATIC HEADER */}
            <div className="p-8 border-b-4 border-black">
                <h1 className="text-3xl font-black">{jobTitle}</h1>
                <p className="font-bold mt-2">{company}</p>
                <p className="text-sm">{location}</p>

                <p className="text-sm mt-1">Applied {appliedDateText}</p>

                {/* Status */}
                <p className="mt-3 font-black uppercase">
                    Status: <span className="underline">{statusLabel[status] || status}</span>
                </p>

                {/* Action Buttons */}
                <div className="flex gap-4 mt-6">
                    <button
                        className="px-6 py-3 border-4 border-black font-black hover:bg-black hover:text-white transition-none"
                        type="button"
                        onClick={() => alert("Withdraw flow will be implemented")}
                    >
                        Withdraw
                    </button>

                    <button
                        className="px-6 py-3 border-4 border-black font-black bg-primary text-white hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none"
                        type="button"
                        onClick={() => alert(`View job: ${application.jobPostId}`)}
                    >
                        View Job →
                    </button>
                </div>
            </div>

            {/* SCROLLABLE DETAILS */}
            <div className="flex-1 overflow-y-auto p-8 space-y-8">
                {/* Documents */}
                <section>
                    <h2 className="text-xl font-black uppercase mb-3">Documents Submitted</h2>

                    {docs.length === 0 ? (
                        <p className="font-bold text-gray-600">No documents uploaded.</p>
                    ) : (
                        <div className="space-y-3">
                            {docs.map((doc) => {
                                const displayId = doc.fileId?.slice?.(0, 8) || "file";
                                const filename = `cv_${displayId}.pdf`;

                                // ✅ Use "fl_attachment" version for downloading
                                const downloadUrl = toDownloadUrl(doc.fileUrl);

                                return (
                                    <div
                                        key={doc.fileId || doc.fileUrl}
                                        className="border-4 border-black p-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
                                    >
                                        <p className="font-black uppercase">
                                            {doc.fileType || "FILE"}{" "}
                                            <span className="text-gray-600 font-semibold normal-case">
                        ({displayId})
                      </span>
                                        </p>

                                        <p className="text-sm font-bold mt-1">
                                            Uploaded: {formatDate(doc.createdAt)}
                                        </p>

                                        {doc.fileUrl && (
                                            <div className="flex gap-4 mt-3 flex-wrap">
                                                {/* ✅ PDF Preview (opens a real PDF tab) */}
                                                <button
                                                    type="button"
                                                    className="font-black uppercase underline"
                                                    onClick={() => openPdf(doc.fileUrl)}
                                                >
                                                    Open
                                                </button>

                                                {/* ✅ Download as REAL .pdf filename */}
                                                <button
                                                    type="button"
                                                    className="font-black uppercase underline"
                                                    onClick={() => downloadPdf(downloadUrl, filename)}
                                                >
                                                    Download PDF
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </section>

                {/* Feedback */}
                <section>
                    <h2 className="text-xl font-black uppercase mb-3">Feedback</h2>
                    <p className="font-bold">
                        {application.feedback ? application.feedback : "No feedback yet."}
                    </p>
                </section>

                {/* Timeline */}
                <section>
                    <h2 className="text-xl font-black uppercase mb-3">Application Timeline</h2>
                    <ul className="list-disc ml-6 font-bold leading-relaxed">
                        {timeline.map((t) => (
                            <li key={t}>{t}</li>
                        ))}
                    </ul>
                </section>
            </div>
        </div>
    );
}

export default ApplicationDetailPanel;
