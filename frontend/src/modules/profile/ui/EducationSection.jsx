// src/modules/profile/ui/EducationSection.jsx
import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import { useResume } from "../hooks/useResume";
import { useSelector } from "react-redux";
import { useState, useEffect } from "react";

function EducationSection() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const { resume, loading, error, updateResume } = useResume(applicantId);

  const [isEditing, setIsEditing] = useState(false);
  const [localEducations, setLocalEducations] = useState([]);
  const [saving, setSaving] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  // Sync local state when resume data loads
  useEffect(() => {
    if (resume?.education) {
      setLocalEducations(
          resume.education.map((edu, index) => ({
            ...edu,
            educationId: edu.educationId || `temp-${index}`, // fallback temp ID for React key
            applicantId: edu.applicantId || applicantId,
          }))
      );
    }
  }, [resume, applicantId]);

  // Add new empty education entry
  const handleAdd = () => {
    setLocalEducations([
      ...localEducations,
      {
        educationId: null,
        applicantId,
        institution: "",
        degree: "",
        fromYear: "",
        toYear: "",
        gpa: "",
      },
    ]);
    setIsEditing(true);
  };

  // Update a field in local state
  const handleChange = (index, field, value) => {
    const updated = [...localEducations];
    updated[index] = { ...updated[index], [field]: value };
    setLocalEducations(updated);
  };

  // Delete education entry
  const handleDelete = (index) => {
    if (!window.confirm("Delete this education entry?")) return;

    const updated = localEducations.filter((_, i) => i !== index);
    setLocalEducations(updated);

    // Save to backend immediately
    handleSave(updated);
  };

  // Save all educations to backend
  const handleSave = async (educationsToSave = localEducations) => {
    setSaving(true);
    try {
      await updateResume({ education: educationsToSave });
      alert("Education saved successfully!");
      setIsEditing(false);
    } catch (err) {
      alert("Failed to save: " + err.message);
    } finally {
      setSaving(false);
    }
  };

  // Cancel editing → reset to original
  const handleCancel = () => {
    setLocalEducations(resume?.education || []);
    setIsEditing(false);
    setErrorMsg("");
  };

  // Loading / Error states
  if (loading) {
    return (
        <SectionWrapper title="Education">
          <p className="text-center py-6">Loading education...</p>
        </SectionWrapper>
    );
  }

  if (error) {
    return (
        <SectionWrapper title="Education">
          <p className="text-center py-6 text-red-600">Error: {error}</p>
        </SectionWrapper>
    );
  }

  return (
      <SectionWrapper
          title="Education"
          onEdit={() => setIsEditing(true)}
          onAdd={handleAdd}
          showEditButtons={!isEditing}
      >
        <div className="space-y-8">
          {isEditing ? (
              <>
                {localEducations.map((edu, index) => (
                    <div
                        key={index}
                        className="border-4 border-black p-6 rounded-lg bg-gray-50 space-y-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
                    >
                      <input
                          type="text"
                          placeholder="Institution"
                          value={edu.institution}
                          onChange={(e) => handleChange(index, "institution", e.target.value)}
                          className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                      />

                      <input
                          type="text"
                          placeholder="Degree"
                          value={edu.degree}
                          onChange={(e) => handleChange(index, "degree", e.target.value)}
                          className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                      />

                      <div className="grid grid-cols-2 gap-4">
                        <input
                            type="number"
                            placeholder="From Year (e.g. 2021)"
                            value={edu.fromYear}
                            onChange={(e) => handleChange(index, "fromYear", e.target.value)}
                            className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                        />

                        <input
                            type="number"
                            placeholder="To Year (or Present)"
                            value={edu.toYear}
                            onChange={(e) => handleChange(index, "toYear", e.target.value)}
                            className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                        />
                      </div>

                      <input
                          type="number"
                          min="0"
                          max="100"
                          step="0.1"
                          placeholder="GPA (optional, 0-100)"
                          value={edu.gpa}
                          onChange={(e) => handleChange(index, "gpa", e.target.value)}
                          className="w-full p-3 border-2 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                      />

                      <button
                          onClick={() => handleDelete(index)}
                          className="text-red-600 hover:text-red-800 font-bold mt-2"
                      >
                        Delete Education
                      </button>
                    </div>
                ))}

                {/* Action buttons */}
                <div className="flex gap-4 mt-8">
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
                {localEducations.length > 0 ? (
                    localEducations.map((edu, index) => (
                        <div
                            key={index}
                            className="border-b-4 border-black pb-6 last:border-b-0"
                        >
                          <h4 className="font-black text-xl">{edu.institution || "Institution"}</h4>
                          <p className="font-bold text-lg mt-1">{edu.degree || "Degree"}</p>
                          <p className="text-sm text-gray-600 mt-1">
                            {edu.fromYear} - {edu.toYear || "Present"}
                          </p>
                          {edu.gpa && (
                              <p className="text-sm text-gray-700 mt-2">GPA: {edu.gpa}</p>
                          )}
                        </div>
                    ))
                ) : (
                    <p className="text-center text-gray-500 py-8 font-semibold">
                      No education entries added yet.
                    </p>
                )}
              </>
          )}
        </div>
      </SectionWrapper>
  );
}

export default EducationSection;