import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";

function ContactSection() {
  const links = ["github.com", "facebook.com", "x.com"];

  return (
    <SectionWrapper
      title="Contact"
      onEdit={() => console.log("edit contact")}
      onAdd={() => console.log("add contact link")}
    >
      <div className="space-y-3 font-bold">
        {links.map((link, i) => (
          <p key={i}>{link}</p>
        ))}
      </div>
    </SectionWrapper>
  );
}

export default ContactSection;
