import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";

function ExperienceSection() {
  return (
    <SectionWrapper 
      title="Experience"
      onEdit={() => console.log("edit experience")}
      onAdd={() => console.log("add experience")}
    >
      <div className="space-y-6">

        <div>
          <h4 className="font-black">Software Engineer Intern</h4>
          <p className="font-bold">Netway Technology — Internship</p>
          <p className="text-sm text-gray-600">Oct 2023 – Feb 2024 · Hybrid</p>
        </div>

        <div>
          <h4 className="font-black">Machine Learning Intern</h4>
          <p className="font-bold">PetroVietnam — Internship</p>
          <p className="text-sm text-gray-600">Oct 2022 – Mar 2023 · Remote</p>
        </div>

      </div>
    </SectionWrapper>
  );
}

export default ExperienceSection;
