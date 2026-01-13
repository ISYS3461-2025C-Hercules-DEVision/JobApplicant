// src/modules/profile/ui/ExperienceSection.jsx
import { useResume } from "../hooks/useResume";
import { useSelector } from "react-redux";
import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import {useEffect, useState} from "react";

function ExperienceSection() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const { resume, loading, error, updateResume } = useResume(applicantId);

  const [isEditing, setIsEditing] = useState(false);
  const [localExperiences, setLocalExperiences] = useState([]);
  const [saving, setSaving] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  // Sync local state when resume data loads
  useEffect(() => {
    if (resume?.experience) {
      setLocalExperiences(
          resume.experience.map((exp, index) => ({
            ...exp,
            experienceId: exp.experienceId || `temp-${index}`, // fallback temp ID for React key
            applicantId: exp.applicantId || applicantId,
          }))
      );
    }
  }, [resume, applicantId]);

  // Add new empty experience
  const handleAdd = () => {
    setLocalExperiences([
      ...localExperiences,
      {
        workExpId: null,
        applicantId,
        jobTitle: "",
        companyName: "",
        fromYear: "",
        toYear: "",
        description: "",
      },
    ]);
    setIsEditing(true);
  };

  // Update a field in local state
  const handleChange = (index, field, value) => {
    const updated = [...localExperiences];
    updated[index] = { ...updated[index], [field]: value };
    setLocalExperiences(updated);
  };

  // Delete experience locally + backend
  const deleteExperience = (index) => {
    if (!window.confirm("Delete this experience?")) return;

    const updated = localExperiences.filter((_, i) => i !== index);
    setLocalExperiences(updated);

    // Save to backend immediately
    handleSave(updated);
  };

  // Save all experiences to backend
  const handleSave = async (experiencesToSave = localExperiences) => {
    setSaving(true);
    try {
      await updateResume({ experience: experiencesToSave });
      alert("Experience saved successfully!");
      setIsEditing(false);
    } catch (err) {
      alert("Failed to save: " + err.message);
    } finally {
      setSaving(false);
    }
  };

  // Cancel editing → reset to original
  const handleCancel = () => {
    setLocalExperiences(resume?.experience || []);
    setIsEditing(false);
    setErrorMsg("");
  };

  // Loading / Error states
  if (loading) {
    return (
        <SectionWrapper title="Experience">
          <p className="text-center py-6">Loading experiences...</p>
        </SectionWrapper>
    );
  }

  if (error) {
    return (
        <SectionWrapper title="Experience">
          <p className="text-center py-6 text-red-600">Error: {error}</p>
        </SectionWrapper>
    );
  }

  return (
      <SectionWrapper
          title="Experience"
          onEdit={() => setIsEditing(true)}
          onAdd={handleAdd}
          showEditButtons={!isEditing}
      >
        <div className="space-y-6">
          {isEditing ? (
              <>
                {localExperiences.map((exp, index) => (
                    <div
                        key={index}
                        className="border-4 border-black p-6 rounded-lg bg-gray-50 space-y-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
                    >
                      <input
                          type="text"
                          placeholder="Job Title"
                          value={exp.jobTitle}
                          onChange={(e) => handleChange(index, "jobTitle", e.target.value)}
                          className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                      />

                      <input
                          type="text"
                          placeholder="Company Name"
                          value={exp.companyName}
                          onChange={(e) => handleChange(index, "companyName", e.target.value)}
                          className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                      />

                      <div className="grid grid-cols-2 gap-4">
                        <input
                            type="text"
                            placeholder="From Year (e.g. 2022)"
                            value={exp.fromYear}
                            onChange={(e) => handleChange(index, "fromYear", e.target.value)}
                            className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                        />

                        <input
                            type="text"
                            placeholder="To Year (or Present)"
                            value={exp.toYear}
                            onChange={(e) => handleChange(index, "toYear", e.target.value)}
                            className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                        />
                      </div>

                      <textarea
                          placeholder="Description"
                          value={exp.description}
                          onChange={(e) => handleChange(index, "description", e.target.value)}
                          className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary min-h-[100px]"
                      />

                      <button
                          onClick={() => deleteExperience(index)}
                          className="text-red-600 hover:text-red-800 font-bold"
                      >
                        Delete Experience
                      </button>
                    </div>
                ))}

                {/* Action buttons */}
                <div className="flex gap-4 mt-6">
                  <button
                      onClick={() => handleSave()}
                      disabled={saving}
                      className="px-8 py-4 bg-primary text-white font-black border-4 border-black rounded hover:bg-blue-700 disabled:opacity-50"
                  >
                    {saving ? "Saving..." : "Save Changes"}
                  </button>

                  <button
                      onClick={handleCancel}
                      className="px-8 py-4 bg-gray-300 text-black font-black border-4 border-black rounded hover:bg-gray-400"
                  >
                    Cancel
                  </button>
                </div>
              </>
          ) : (
              <>
                {localExperiences.length > 0 ? (
                    localExperiences.map((exp, index) => (
                        <div key={index} className="border-b-4 border-black pb-6 last:border-b-0">
                          <h4 className="font-black text-xl">{exp.jobTitle || "Untitled Position"}</h4>
                          <p className="font-bold text-lg mt-1">{exp.companyName || "Company Name"}</p>
                          <p className="text-sm text-gray-600 mt-1">
                            {exp.fromYear} - {exp.toYear || "Present"}
                          </p>
                          {exp.description && (
                              <p className="mt-3 text-gray-700 whitespace-pre-line">{exp.description}</p>
                          )}
                        </div>
                    ))
                ) : (
                    <p className="text-center text-gray-500 py-8 font-semibold">
                      No experiences added yet.
                    </p>
                )}
              </>
          )}
        </div>
      </SectionWrapper>
  );
}

export default ExperienceSection;
