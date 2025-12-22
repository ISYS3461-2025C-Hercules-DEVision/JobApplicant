function NotificationItem({ data }) {
  return (
    <div
      className="
        bg-white border-4 border-black p-6
        shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]
        hover:translate-x-1 hover:translate-y-1 hover:shadow-none
        transition-none
      "
    >
      <div className="flex justify-between items-start">
        <div>
          <h3 className="text-xl font-black uppercase mb-1">
            {data.jobTitle}
          </h3>
          <p className="font-bold">
            {data.company} — {data.location}
          </p>
        </div>

        <span className="text-sm font-bold text-primary">
          {data.time}
        </span>
      </div>

      <div className="mt-4">
        <p className="font-bold mb-2">Matched skills:</p>
        <div className="flex flex-wrap gap-2">
          {data.matchedSkills.map((skill) => (
            <span
              key={skill}
              className="
                px-3 py-1 border-2 border-black font-bold
                hover:bg-primary hover:text-white
              "
            >
              {skill}
            </span>
          ))}
        </div>
      </div>

      <div className="mt-4 flex gap-4">
        <button
          className="
            border-4 border-black px-6 py-2 font-black
            hover:bg-black hover:text-white
            transition-none
          "
        >
          View Job
        </button>

        <button
          className="
            bg-primary text-white border-4 border-black px-6 py-2 font-black
            hover:translate-x-1 hover:translate-y-1 hover:shadow-none
            shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]
            transition-none
          "
        >
          Apply Now
        </button>
      </div>
    </div>
  );
}

export default NotificationItem;
