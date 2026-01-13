// src/modules/profile/ui/SkillsSection.jsx
import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import { useResume } from "../hooks/useResume";
import { useSelector } from "react-redux";
import { useState, useEffect } from "react";

function SkillsSection() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const { resume, loading, error, updateResume } = useResume(applicantId);

  const [localSkills, setLocalSkills] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [newSkill, setNewSkill] = useState("");
  const [saving, setSaving] = useState(false);

  // Sync local state when resume data loads
  useEffect(() => {
    if (resume?.skills) {
      setLocalSkills([...resume.skills]); // copy array to avoid mutation
    }
  }, [resume]);

  // Add new skill
  const handleAddSkill = () => {
    if (!newSkill.trim()) return;

    const cleaned = newSkill.trim();
    if (localSkills.includes(cleaned)) return; // avoid duplicates

    setLocalSkills([...localSkills, cleaned]);
    setNewSkill("");
  };

  // Remove skill
  const handleRemoveSkill = (skillToRemove) => {
    setLocalSkills(localSkills.filter(skill => skill !== skillToRemove));
  };

  // Save changes to backend
  const handleSave = async () => {
    setSaving(true);
    try {
      await updateResume({ skills: localSkills });
      alert("Skills updated successfully!");
      setIsEditing(false);
    } catch (err) {
      alert("Failed to save: " + err.message);
    } finally {
      setSaving(false);
    }
  };

  // Cancel → reset to original
  const handleCancel = () => {
    setLocalSkills(resume?.skills || []);
    setIsEditing(false);
    setNewSkill("");
  };

  // Loading / Error states
  if (loading) {
    return (
        <SectionWrapper title="Skills">
          <p className="text-center py-6">Loading skills...</p>
        </SectionWrapper>
    );
  }

  if (error) {
    return (
        <SectionWrapper title="Skills">
          <p className="text-center py-6 text-red-600">Error: {error}</p>
        </SectionWrapper>
    );
  }

  return (
      <SectionWrapper
          title="Skills"
          onEdit={() => setIsEditing(true)}
          onAdd={() => setIsEditing(true)} // optional - can trigger add input focus if you want
          showEditButtons={!isEditing}
      >
        <div className="space-y-6">
          {isEditing ? (
              <div className="space-y-4">
                {/* Current skills tags */}
                <div className="flex flex-wrap gap-3">
                  {localSkills.map((skill) => (
                      <div
                          key={skill}
                          className="flex items-center gap-2 px-4 py-2 border-2 border-black font-bold rounded-md bg-white text-black"
                      >
                        {skill}
                        <button
                            onClick={() => handleRemoveSkill(skill)}
                            className="text-red-600 hover:text-red-800 font-black text-lg leading-none"
                        >
                          ×
                        </button>
                      </div>
                  ))}
                </div>

                {/* Add new skill input */}
                <div className="flex gap-3">
                  <input
                      type="text"
                      value={newSkill}
                      onChange={(e) => setNewSkill(e.target.value)}
                      onKeyDown={(e) => {
                        if (e.key === "Enter") {
                          e.preventDefault();
                          handleAddSkill();
                        }
                      }}
                      placeholder="Add new skill (press Enter)"
                      className="flex-1 px-4 py-3 border-4 border-black rounded font-bold focus:outline-none focus:ring-2 focus:ring-primary"
                  />
                  <button
                      onClick={handleAddSkill}
                      className="px-6 py-3 bg-primary text-white font-black border-4 border-black rounded hover:bg-blue-700"
                  >
                    Add
                  </button>
                </div>

                {/* Save / Cancel */}
                <div className="flex gap-4 mt-6">
                  <button
                      onClick={handleSave}
                      disabled={saving}
                      className="px-8 py-4 bg-green-600 text-white font-black border-4 border-black rounded hover:bg-green-700 disabled:opacity-50"
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
              </div>
          ) : (
              <div className="flex flex-wrap gap-3">
                {localSkills.length > 0 ? (
                    localSkills.map((skill) => (
                        <span
                            key={skill}
                            className="
                    px-5 py-2 border-4 border-black font-black rounded-md
                    bg-white text-black
                    hover:bg-primary hover:text-white
                    transition-none
                  "
                        >
                  {skill}
                </span>
                    ))
                ) : (
                    <p className="text-gray-500 font-semibold">
                      No skills added yet.
                    </p>
                )}
              </div>
          )}
        </div>
      </SectionWrapper>
  );
}

export default SkillsSection;