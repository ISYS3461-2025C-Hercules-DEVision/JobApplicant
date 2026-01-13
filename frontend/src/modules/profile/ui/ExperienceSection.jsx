// src/modules/profile/ui/ExperienceSection.jsx
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";

import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import { useResume } from "../hooks/useResume";
import { updateExperiencesSchema } from "../../../schemas/profileSchema";

function firstZodMessage(zodError) {
  return zodError?.issues?.[0]?.message || "Invalid data.";
}

function normalizeExperience(exp, applicantId, index) {
  return {
    ...exp,
    workExpId: exp?.workExpId ?? null,
    applicantId: exp?.applicantId ?? applicantId,
    jobTitle: exp?.jobTitle ?? "",
    companyName: exp?.companyName ?? "",
    fromYear: exp?.fromYear ?? "",
    toYear: exp?.toYear ?? "",
    description: exp?.description ?? "",
    experienceId: exp?.experienceId || exp?.workExpId || `temp-${index}`,
  };
}

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
        resume.experience.map((exp, index) =>
          normalizeExperience(exp, applicantId, index)
        )
      );
    } else {
      setLocalExperiences([]);
    }
  }, [resume, applicantId]);

  // Add new empty experience
  const handleAdd = () => {
    setLocalExperiences((prev) => [
      ...prev,
      {
        workExpId: null,
        applicantId,
        jobTitle: "",
        companyName: "",
        fromYear: "",
        toYear: "",
        description: "",
        experienceId: `temp-${Date.now()}`,
      },
    ]);
    setIsEditing(true);
    setErrorMsg("");
  };

  // Update a field in local state
  const handleChange = (index, field, value) => {
    setLocalExperiences((prev) => {
      const updated = [...prev];
      updated[index] = { ...updated[index], [field]: value };
      return updated;
    });
  };

  // Save all experiences to backend (resume)
  const handleSave = async (experiencesToSave = localExperiences) => {
    setErrorMsg("");

    const parsed = updateExperiencesSchema.safeParse({
      experiences: experiencesToSave,
    });

    if (!parsed.success) {
      setErrorMsg(firstZodMessage(parsed.error));
      return;
    }

    setSaving(true);
    try {
      await updateResume({ experience: parsed.data.experiences });
      setIsEditing(false);
    } catch (err) {
      setErrorMsg(err?.message || "Failed to save experience.");
    } finally {
      setSaving(false);
    }
  };

  // Delete experience locally + save immediately
  const deleteExperience = async (index) => {
    setErrorMsg("");
    if (!window.confirm("Delete this experience?")) return;

    const updated = localExperiences.filter((_, i) => i !== index);
    setLocalExperiences(updated);
    await handleSave(updated);
  };

  // Cancel editing → reset to original (normalize again to avoid undefined fields)
  const handleCancel = () => {
    const original = (resume?.experience || []).map((exp, index) =>
      normalizeExperience(exp, applicantId, index)
    );
    setLocalExperiences(original);
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
    const msg = typeof error === "string" ? error : error?.message;
    return (
      <SectionWrapper title="Experience">
        <p className="text-center py-6 text-red-600">
          Error: {msg || "Unknown error"}
        </p>
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
      {errorMsg && (
        <div className="mb-3 p-3 border-2 border-red-600 bg-red-50 font-bold">
          {errorMsg}
        </div>
      )}

      <div className="space-y-6">
        {isEditing ? (
          <>
            {localExperiences.map((exp, index) => (
              <div
                key={exp.experienceId ?? index}
                className="border-4 border-black p-6 rounded-lg bg-gray-50 space-y-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
              >
                <input
                  type="text"
                  placeholder="Job Title"
                  value={exp.jobTitle}
                  onChange={(e) =>
                    handleChange(index, "jobTitle", e.target.value)
                  }
                  className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                />

                <input
                  type="text"
                  placeholder="Company Name"
                  value={exp.companyName}
                  onChange={(e) =>
                    handleChange(index, "companyName", e.target.value)
                  }
                  className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                />

                <div className="grid grid-cols-2 gap-4">
                  <input
                    type="text"
                    placeholder="From Year (e.g. 2022)"
                    value={exp.fromYear}
                    onChange={(e) =>
                      handleChange(index, "fromYear", e.target.value)
                    }
                    className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                  />

                  <input
                    type="text"
                    placeholder="To Year (or Present)"
                    value={exp.toYear}
                    onChange={(e) =>
                      handleChange(index, "toYear", e.target.value)
                    }
                    className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                  />
                </div>

                <textarea
                  placeholder="Description"
                  value={exp.description}
                  onChange={(e) =>
                    handleChange(index, "description", e.target.value)
                  }
                  className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary min-h-[100px]"
                />

                <button
                  type="button"
                  onClick={() => deleteExperience(index)}
                  className="text-red-600 hover:text-red-800 font-bold"
                >
                  Delete Experience
                </button>
              </div>
            ))}

            <div className="flex gap-4 mt-6">
              <button
                type="button"
                onClick={() => handleSave()}
                disabled={saving}
                className="px-8 py-4 bg-primary text-white font-black border-4 border-black rounded hover:bg-blue-700 disabled:opacity-50"
              >
                {saving ? "Saving..." : "Save Changes"}
              </button>

              <button
                type="button"
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
                <div
                  key={exp.experienceId ?? index}
                  className="border-b-4 border-black pb-6 last:border-b-0"
                >
                  <h4 className="font-black text-xl">
                    {exp.jobTitle || "Untitled Position"}
                  </h4>
                  <p className="font-bold text-lg mt-1">
                    {exp.companyName || "Company Name"}
                  </p>
                  <p className="text-sm text-gray-600 mt-1">
                    {exp.fromYear} - {exp.toYear || "Present"}
                  </p>
                  {exp.description && (
                    <p className="mt-3 text-gray-700 whitespace-pre-line">
                      {exp.description}
                    </p>
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
