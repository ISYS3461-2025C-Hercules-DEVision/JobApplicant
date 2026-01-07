import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import SkillTagInput from "./SkillTagInput";
import EmploymentTypeSelector from "./EmploymentTypeSelector";
import SalaryRangeInput from "./SalaryRangeInput";
import JobTitleInput from "./JobTitleInput";
import { searchProfileService } from "../services/searchProfileService";

function SearchProfileForm({ isPremium = false }) {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;
  const [skills, setSkills] = useState([]);
  const [employmentTypes, setEmploymentTypes] = useState([]);
  const [salaryRange, setSalaryRange] = useState({ min: "", max: "" });
  const [jobTitles, setJobTitles] = useState("");
  const [country, setCountry] = useState("");

  useEffect(() => {
    if (!applicantId) return;
    searchProfileService
      .get(applicantId)
      .then((res) => {
        setSkills(res.technicalTags || []);
        // Map enums to UI labels
        const mapStatus = (s) =>
          s === "FULL_TIME"
            ? "Full-time"
            : s === "PART_TIME"
            ? "Part-time"
            : s === "FRESHER"
            ? "Fresher"
            : s === "INTERNSHIP"
            ? "Internship"
            : s === "CONTRACT"
            ? "Contract"
            : s;

        setEmploymentTypes((res.employmentStatuses || []).map(mapStatus));
        setCountry(res.country || "");
        setSalaryRange({
          min: res.minSalary ?? "",
          max: res.maxSalary ?? "",
        });
        setJobTitles((res.desiredJobTitles || []).join("; "));
      })
      .catch(() => {});
  }, [applicantId]);

  const handleSave = async () => {
    if (!applicantId) return;
    const employmentStatuses = (employmentTypes || []).map((t) =>
      t.replace(/\s+/g, "_").replace("-", "_").toUpperCase()
    );

    const payload = {
      technicalTags: skills,
      employmentStatuses,
      country,
      minSalary: salaryRange.min ? parseInt(salaryRange.min, 10) : 0,
      maxSalary: salaryRange.max ? parseInt(salaryRange.max, 10) : null,
      jobTitles: jobTitles || "",
    };

    const res = await searchProfileService.upsert(applicantId, payload);
    setSkills(res.technicalTags || []);
    setEmploymentTypes(
      (res.employmentStatuses || []).map((s) =>
        s === "FULL_TIME"
          ? "Full-time"
          : s === "PART_TIME"
          ? "Part-time"
          : s === "FRESHER"
          ? "Fresher"
          : s === "INTERNSHIP"
          ? "Internship"
          : s === "CONTRACT"
          ? "Contract"
          : s
      )
    );
    setCountry(res.country || "");
    setSalaryRange({
      min: res.minSalary ?? "",
      max: res.maxSalary ?? "",
    });
    setJobTitles((res.desiredJobTitles || []).join("; "));

    alert("Search profile saved successfully!");
  };

  return (
    <>
      <SkillTagInput skills={skills} setSkills={setSkills} />

      <EmploymentTypeSelector
        selected={employmentTypes}
        setSelected={setEmploymentTypes}
      />

      <SalaryRangeInput value={salaryRange} onChange={setSalaryRange} />

      <div className="bg-white border-4 border-black p-6 mb-8 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]">
        <h2 className="text-2xl font-black uppercase mb-4">Location</h2>

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

      {isPremium ? (
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
      ) : (
        <a
          href="/subscription"
          className="
            block w-full text-center bg-black text-white font-black uppercase
            border-4 border-black py-4
            hover:translate-x-1 hover:translate-y-1 hover:shadow-none
            shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]
            transition-none
          "
        >
          Subscribe to save search profile
        </a>
      )}
    </>
  );
}

export default SearchProfileForm;
