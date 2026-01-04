function ApplicationDetailPanel({ application, loading = false }) {
  if (loading) {
    return (
      <div className="bg-white border-4 border-black p-10 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] text-center font-black text-xl">
        Loading...
      </div>
    );
  }

  if (!application) {
    return (
      <div className="bg-white border-4 border-black p-10 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] text-center font-black text-xl">
        Select an application to view details
      </div>
    );
  }

  const createdAtText = application.createdAt
    ? new Date(application.createdAt).toLocaleString()
    : "N/A";

  const statusText = typeof application.status === "string"
    ? application.status
    : (application.status?.toString?.() ?? "N/A");

  const cv = application.applicantCV;
  const cover = application.coverLetter;

  return (
    <div className="bg-white border-4 border-black shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] h-[80vh] flex flex-col">
      {/* STATIC HEADER */}
      <div className="p-8 border-b-4 border-black">
        {/* Vì backend chưa có jobTitle/location nên hiển thị ID trước */}
        <h1 className="text-3xl font-black">
          Application #{application.applicationId ?? application.id}
        </h1>

        <p className="font-bold mt-2">
          Company ID: {application.companyId ?? "N/A"}
        </p>

        <p className="text-sm">
          JobPost ID: {application.jobPostId ?? "N/A"}
        </p>

        <p className="text-sm mt-1">
          Created at: {createdAtText}
        </p>

        {/* Action Buttons (tạm thời để UI, backend chưa có withdraw/view job) */}
        <div className="flex gap-4 mt-6">
          <button
            disabled
            className="px-6 py-3 border-4 border-black font-black opacity-50 cursor-not-allowed"
            title="Not implemented yet"
          >
            Withdraw
          </button>

          <button
            disabled
            className="px-6 py-3 border-4 border-black font-black bg-primary text-white opacity-50 cursor-not-allowed shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none"
            title="Job detail comes from JM later"
          >
            View Job →
          </button>
        </div>
      </div>

      {/* SCROLLABLE DETAILS */}
      <div className="flex-1 overflow-y-auto p-8 space-y-8">
        <section>
          <h2 className="text-xl font-black uppercase mb-3">Status</h2>
          <p className="font-bold">{statusText}</p>
        </section>

        <section>
          <h2 className="text-xl font-black uppercase mb-3">Resume Submitted</h2>
          {cv?.fileUrl ? (
            <div className="space-y-2">
              <p className="font-bold">File ID: {cv.fileId ?? "N/A"}</p>
              <p className="font-bold">Type: {cv.fileType ?? "N/A"}</p>
              <a
                className="font-black underline"
                href={cv.fileUrl}
                target="_blank"
                rel="noreferrer"
              >
                Open CV File
              </a>
            </div>
          ) : (
            <p className="font-bold">No CV uploaded</p>
          )}
        </section>

        <section>
          <h2 className="text-xl font-black uppercase mb-3">Cover Letter File</h2>
          {cover?.fileUrl ? (
            <div className="space-y-2">
              <p className="font-bold">File ID: {cover.fileId ?? "N/A"}</p>
              <p className="font-bold">Type: {cover.fileType ?? "N/A"}</p>
              <a
                className="font-black underline"
                href={cover.fileUrl}
                target="_blank"
                rel="noreferrer"
              >
                Open Cover Letter File
              </a>
            </div>
          ) : (
            <p className="font-bold">No cover letter uploaded</p>
          )}
        </section>

        <section>
          <h2 className="text-xl font-black uppercase mb-3">Application Timeline</h2>
          <ul className="list-disc ml-6 font-bold leading-relaxed">
            <li>Application Created</li>
            <li>Created at: {createdAtText}</li>
            <li>Current status: {statusText}</li>
          </ul>
        </section>
      </div>
    </div>
  );
}

export default ApplicationDetailPanel;
