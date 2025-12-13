import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";

function AboutSection() {
  return (
    <SectionWrapper 
      title="About"
      onEdit={() => console.log("edit about")}
    >
      <p className="font-bold leading-relaxed">
        I am a Software Engineering student with a passion for frontend
        development, UI/UX design, and modern JavaScript engineering.
      </p>
    </SectionWrapper>
  );
}

export default AboutSection;
