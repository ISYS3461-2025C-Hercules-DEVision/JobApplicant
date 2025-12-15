function ApplicationDetailPanel({ application }) {

  if (!application) {
    return (
      <div className="bg-white border-4 border-black p-10 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] text-center font-black text-xl">
        Select an application to view details
      </div>
    );
  }

  return (
    <div className="bg-white border-4 border-black shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] h-[80vh] flex flex-col">

      {/* STATIC HEADER */}
      <div className="p-8 border-b-4 border-black">
        <h1 className="text-3xl font-black">{application.jobTitle}</h1>
        <p className="font-bold mt-2">{application.company}</p>
        <p className="text-sm">{application.location}</p>

        <p className="text-sm mt-1">
          Applied {application.appliedDate}
        </p>

        {/* Action Buttons */}
        <div className="flex gap-4 mt-6">
          <button className="px-6 py-3 border-4 border-black font-black hover:bg-black hover:text-white transition-none">
            Withdraw
          </button>

          <button className="px-6 py-3 border-4 border-black font-black bg-primary text-white hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none">
            View Job →
          </button>
        </div>
      </div>

      {/* SCROLLABLE DETAILS */}
      <div className="flex-1 overflow-y-auto p-8 space-y-8">

        <section>
          <h2 className="text-xl font-black uppercase mb-3">Resume Submitted</h2>
          <p className="font-bold">resume_johndoe.pdf</p>
        </section>

        <section>
          <h2 className="text-xl font-black uppercase mb-3">Cover Letter</h2>
          <p>
            Thank you for reviewing my application. I am confident that my experience in...
          </p>
        </section>

        <section>
          <h2 className="text-xl font-black uppercase mb-3">Application Timeline</h2>
          <ul className="list-disc ml-6 font-bold leading-relaxed">
            <li>Application Submitted</li>
            <li>Viewed by Recruiter</li>
            <li>{application.status}</li>
          </ul>
        </section>

      </div>
    </div>
  );
}

export default ApplicationDetailPanel;
