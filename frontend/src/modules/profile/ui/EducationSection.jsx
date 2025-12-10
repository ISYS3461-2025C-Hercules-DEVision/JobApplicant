import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";

function EducationSection() {
  return (
    <SectionWrapper
      title="Education"
      onEdit={() => console.log("edit education")}
      onAdd={() => console.log("add education")}
    >
      <div className="space-y-6">

        <div>
          <h4 className="font-black">RMIT University Vietnam</h4>
          <p className="font-bold">Bachelor of Software Engineering (Honours)</p>
          <p className="text-sm text-gray-600">2021 – 2025</p>
          <p className="text-sm">Media Club · Fintech Club · NXC Culture</p>
        </div>

        <div>
          <h4 className="font-black">American International School</h4>
          <p className="font-bold">High School Diploma</p>
          <p className="text-sm text-gray-600">2018 – 2021</p>
        </div>

      </div>
    </SectionWrapper>
  );
}

export default EducationSection;
