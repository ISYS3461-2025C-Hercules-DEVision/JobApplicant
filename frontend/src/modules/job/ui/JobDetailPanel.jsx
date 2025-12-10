function JobDetailPanel({ job }) {

  if (!job) {
    return (
      <div className="bg-white border-4 border-black p-10 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] text-center font-black text-xl">
        Select a job from the list to view details
      </div>
    );
  }

  return (
    <div className="bg-white border-4 border-black shadow-[6px_6px_0px_0px_rgba(0,0,0,1)] h-[80vh] flex flex-col">

      {/* STATIC TOP SECTION */}
      <div className="p-8 border-b-4 border-black">
        <h1 className="text-3xl font-black">{job.title}</h1>
        <p className="font-bold mt-2">{job.company}</p>
        <p className="text-sm text-gray-700">{job.location}</p>

        <p className="text-sm mt-1">
          Posted {job.posted} · <span className="text-primary">15 people clicked apply</span>
        </p>

        {/* Buttons */}
        <div className="flex gap-4 mt-6">
          <button className="px-6 py-3 border-4 border-black font-black hover:bg-black hover:text-white transition-none">
            Save
          </button>
          <button className="px-6 py-3 border-4 border-black font-black bg-primary text-white hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none">
            Apply →
          </button>
        </div>
      </div>

      {/* SCROLLABLE CONTENT */}
      <div className="flex-1 overflow-y-auto p-8 space-y-8">

        <section>
          <h2 className="text-xl font-black uppercase mb-3">About the Job</h2>
          <p className="font-bold leading-relaxed">
            TechNova is looking for a Backend Engineer to help build scalable microservices powering
            next-generation analytics. You will work with a global engineering team…
          </p>
        </section>

        <section>
          <h2 className="text-xl font-black uppercase mb-3">Responsibilities</h2>
          <ul className="list-disc ml-6 font-bold leading-relaxed">
            <li>Develop backend services using Node.js</li>
            <li>Optimize MongoDB queries</li>
            <li>Integrate Kafka message streaming</li>
            <li>Improve API performance</li>
          </ul>
        </section>

        <section>
          <h2 className="text-xl font-black uppercase mb-3">Requirements</h2>
          <ul className="list-disc ml-6 font-bold leading-relaxed">
            <li>1+ year experience with Node.js</li>
            <li>Familiar with MongoDB or PostgreSQL</li>
            <li>Understanding of Express / NestJS</li>
            <li>Experience with CI/CD</li>
          </ul>
        </section>

      </div>
    </div>
  );
}

export default JobDetailPanel;
