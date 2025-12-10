import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";

function SkillsSection() {
  const skills = [
    "Java", "Python", "React", "Swift",
    "C++", "OOP", "HTML", "CSS",
    "JavaScript", "SQL", "MongoDB"
  ];

  return (
    <SectionWrapper
      title="Skills"
      onEdit={() => console.log("edit skills")}
      onAdd={() => console.log("add skill")}
    >
      <div className="flex flex-wrap gap-3">
        {skills.map((s) => (
          <span
            key={s}
            className="
              px-4 py-2 border-2 border-black font-bold rounded-md
              bg-white text-black
              hover:bg-primary hover:text-white
              transition-none
            "
          >
            {s}
          </span>
        ))}
      </div>
    </SectionWrapper>
  );
}

export default SkillsSection;
