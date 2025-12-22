import { useState } from "react";
import SkillTagInput from "./SkillTagInput";
import EmploymentTypeSelector from "./EmploymentTypeSelector";
import SalaryRangeInput from "./SalaryRangeInput";
import JobTitleInput from "./JobTitleInput";

function SearchProfileForm() {
  const [skills, setSkills] = useState([]);
  const [employmentTypes, setEmploymentTypes] = useState([]);
  const [salaryRange, setSalaryRange] = useState({ min: "", max: "" });
  const [jobTitles, setJobTitles] = useState("");
  const [country, setCountry] = useState("");

  const handleSave = () => {
    const payload = {
      skills,
      employmentTypes,
      salaryRange: {
        min: salaryRange.min || 0,
        max: salaryRange.max || null,
      },
      country,
      jobTitles,
    };

    console.log("Search profile saved:", payload);
    alert("Search profile saved successfully!");
  };

  return (
    <>
      <SkillTagInput skills={skills} setSkills={setSkills} />

      <EmploymentTypeSelector
        selected={employmentTypes}
        setSelected={setEmploymentTypes}
      />

      <SalaryRangeInput
        value={salaryRange}
        onChange={setSalaryRange}
      />

      <div className="bg-white border-4 border-black p-6 mb-8 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]">
        <h2 className="text-2xl font-black uppercase mb-4">
          Location
        </h2>

        <select
          value={country}
          onChange={(e) => setCountry(e.target.value)}
          className="w-full border-4 border-black px-3 py-2 font-bold"
        >
          <option value="">Select country</option>
          <option value="Vietnam">Vietnam</option>
          <option value="Australia">Australia</option>
          <option value="Remote">Remote</option>
        </select>
      </div>

      <JobTitleInput value={jobTitles} onChange={setJobTitles} />

      <button
        onClick={handleSave}
        className="
          w-full bg-primary text-white font-black uppercase
          border-4 border-black py-4
          hover:translate-x-1 hover:translate-y-1 hover:shadow-none
          shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]
          transition-none
        "
      >
        Save Search Profile
      </button>
    </>
  );
}

export default SearchProfileForm;
