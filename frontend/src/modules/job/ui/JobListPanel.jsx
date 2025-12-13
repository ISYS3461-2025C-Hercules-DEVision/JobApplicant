function JobListPanel({ onSelectJob, selectedJob }) {

  const jobs = [
    {
      id: 1,
      title: "Software Engineer (Backend)",
      company: "TechNova Solutions",
      location: "Ho Chi Minh City, Vietnam (On-site)",
      skills: "Node.js, MongoDB, Docker",
      posted: "3 days ago",
      expires: "20 days",
    },
    {
      id: 2,
      title: "Frontend Developer (React)",
      company: "BrightWave Digital",
      location: "Da Nang, Vietnam (Hybrid)",
      skills: "React, Redux, TypeScript",
      posted: "1 day ago",
      expires: "10 days",
    },
    {
      id: 3,
      title: "Data Engineer",
      company: "SkyTech Analytics",
      location: "Hanoi, Vietnam (Remote)",
      skills: "Python, Airflow, AWS",
      posted: "5 days ago",
      expires: "14 days",
    },
    {
      id: 4,
      title: "Mobile Developer (Flutter)",
      company: "DreamLab Studio",
      location: "Ho Chi Minh City (On-site)",
      skills: "Flutter, Firebase",
      posted: "Today",
      expires: "30 days",
    },
    {
      id: 5,
      title: "Full Stack Developer",
      company: "CodeSphere",
      location: "Remote",
      skills: "React, Node.js, PostgreSQL",
      posted: "2 days ago",
      expires: "21 days",
    },
    {
      id: 6,
      title: "Frontend Engineer (Next.js)",
      company: "Innovex Labs",
      location: "Remote",
      skills: "Next.js, Tailwind, TypeScript",
      posted: "3 days ago",
      expires: "7 days",
    },
    {
      id: 7,
      title: "Backend Engineer (Java)",
      company: "FinTechX",
      location: "Singapore (Hybrid)",
      skills: "Java, Spring Boot, Kafka",
      posted: "4 days ago",
      expires: "18 days",
    },
    {
      id: 8,
      title: "Cloud Engineer",
      company: "Nimbus Cloud",
      location: "Remote",
      skills: "AWS, Terraform, Kubernetes",
      posted: "1 day ago",
      expires: "12 days",
    },
    {
      id: 9,
      title: "Data Scientist",
      company: "InsightAI",
      location: "Hanoi, Vietnam",
      skills: "Python, Pandas, ML",
      posted: "2 days ago",
      expires: "9 days",
    },
    {
      id: 10,
      title: "DevOps Engineer",
      company: "FlowOps",
      location: "Melbourne, Australia",
      skills: "Docker, Jenkins, Kubernetes",
      posted: "6 days ago",
      expires: "25 days",
    },
  ];

  return (
    <div className="space-y-4">
      {jobs.map(job => {
        const isSelected = selectedJob?.id === job.id;

        return (
          <div
            key={job.id}
            onClick={() => onSelectJob(job)}
            className={`
              cursor-pointer border-4 border-black p-4 
              shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]
              hover:translate-x-1 hover:translate-y-1 hover:shadow-none
              transition-none

              ${isSelected ? "bg-primary text-white" : "bg-white text-black"}
            `}
          >
            <h3 className="text-lg font-black">{job.title}</h3>
            <p className="font-bold">{job.company}</p>
            <p className="text-sm">{job.location}</p>
            <p className="text-sm">Skills: {job.skills}</p>

            {/* Posted + Expires row */}
            <div className="flex justify-between mt-2 text-sm font-bold">
              <span>
                Posted: {job.posted}
              </span>

              <span className={isSelected ? "text-black" : "text-primary"}>
                Expires in {job.expires}
              </span>
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default JobListPanel;
