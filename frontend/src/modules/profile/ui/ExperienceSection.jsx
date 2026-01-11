import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import { useProfile } from "../hooks/useProfile.js";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { updateExperiencesSchema } from "../../../schemas/profileSchema";

function firstZodMessage(zodError) {
  return zodError?.issues?.[0]?.message || "Invalid data.";
}

function ExperienceSection() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const { profile, loading: profileLoading, error: profileError, updateProfile } = useProfile(applicantId);

  const [localExperiences, setLocalExperiences] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    if (profile?.experiences) {
      setLocalExperiences(
        profile.experiences.map((exp) => ({
          workExpId: exp.workExpId || null,
          applicantId: exp.applicantId || applicantId,
          jobTitle: exp.jobTitle || "",
          companyName: exp.companyName || "",
          fromYear: exp.fromYear || "",
          toYear: exp.toYear || "",
          description: exp.description || "",
        }))
      );
    }
  }, [profile, applicantId]);

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
      },
    ]);
    setIsEditing(true);
  };

  const handleChange = (index, field, value) => {
    const updated = [...localExperiences];
    updated[index] = { ...updated[index], [field]: value };
    setLocalExperiences(updated);
  };

  const deleteExperience = async (index) => {
    if (!window.confirm("Delete this experience ?")) return;

    try {
      const updatedExperience = localExperiences.filter((_, i) => i !== index);

      const parsed = updateExperiencesSchema.safeParse({ experiences: updatedExperience });
      if (!parsed.success) {
        setErrorMsg(firstZodMessage(parsed.error));
        return;
      }

      await updateProfile(parsed.data);
      setLocalExperiences(updatedExperience);
      setErrorMsg("");
    } catch (err) {
      setErrorMsg(err?.message || "Failed to delete experience.");
    }
  };

  const handleSave = async () => {
    setErrorMsg("");

    const parsed = updateExperiencesSchema.safeParse({ experiences: localExperiences });
    if (!parsed.success) {
      setErrorMsg(firstZodMessage(parsed.error));
      return;
    }

    setSaving(true);
    try {
      await updateProfile(parsed.data);
      setIsEditing(false);
    } catch (err) {
      setErrorMsg(err?.message || "Failed to save experience.");
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setLocalExperiences(profile?.experiences || []);
    setIsEditing(false);
    setErrorMsg("");
  };

  if (profileLoading) return <p className="text-center py-6">Loading experiences....</p>;
  if (profileError) return <p className="text-center py-6">Error: {profileError.message}</p>;

  return (
    <SectionWrapper title="Experience" onEdit={() => setIsEditing(true)} onAdd={handleAdd} showEditButtons={!isEditing}>
      {errorMsg && (
        <div className="mb-3 p-3 border-2 border-red-600 bg-red-50 font-bold">
          {errorMsg}
        </div>
      )}

      <div className="space-y-6">
        {isEditing ? (
          <>
            {localExperiences.map((exp, index) => (
              <div key={index} className="border p-4 rounded-lg bg-gray-50 space-y-3">
                <input
                  type="text"
                  placeholder="Job Title"
                  value={exp.jobTitle}
                  onChange={(e) => handleChange(index, "jobTitle", e.target.value)}
                  className="w-full p-2 border grounded"
                />
                <input
                  type="text"
                  placeholder="Company Name"
                  value={exp.companyName}
                  onChange={(e) => handleChange(index, "companyName", e.target.value)}
                  className="w-full p-2 border grounded"
                />

                <div className="grid grid-cols-2 gap-4">
                  <input
                    type="text"
                    placeholder="From Year"
                    value={exp.fromYear}
                    onChange={(e) => handleChange(index, "fromYear", e.target.value)}
                    className="w-full p-2 border grounded"
                  />

                  <input
                    type="text"
                    placeholder="To Year"
                    value={exp.toYear}
                    onChange={(e) => handleChange(index, "toYear", e.target.value)}
                    className="w-full p-2 border grounded"
                  />
                </div>

                <textarea
                  placeholder="Description"
                  value={exp.description}
                  onChange={(e) => handleChange(index, "description", e.target.value)}
                  className="w-full p-2 border grounded h-24"
                />

                <button type="button" onClick={() => deleteExperience(index)} className="text-red-600 hover:underline">
                  Delete
                </button>
              </div>
            ))}

            <div className="flex gap-4 mt-4">
              <button
                type="button"
                onClick={handleSave}
                disabled={saving}
                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
              >
                {saving ? "Saving..." : "Save"}
              </button>

              <button type="button" onClick={handleCancel} className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400">
                Cancel
              </button>
            </div>
          </>
        ) : (
          <>
            {localExperiences.length > 0 ? (
              localExperiences.map((exp, index) => (
                <div key={index} className="border-b pb-4">
                  <h4 className="font-bold">{exp.jobTitle}</h4>
                  <p className="text-gray-700">{exp.companyName}</p>
                  <p className="text-sm text-gray-600">
                    {exp.fromYear} - {exp.toYear || "Present"}
                  </p>
                  {exp.description && <p className="mt-2 text-gray-600">{exp.description}</p>}
                </div>
              ))
            ) : (
              <p className="text-gray-500">No Experiences added yet.</p>
            )}
          </>
        )}
      </div>
    </SectionWrapper>
  );
}

export default ExperienceSection;
