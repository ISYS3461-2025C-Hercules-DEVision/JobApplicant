import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import { useState, useEffect } from "react";
import { useProfile } from "../hooks/useProfile.js";
import { useSelector } from "react-redux";
import { updateSkillsSchema } from "../../../schemas/profileSchema";

function firstZodMessage(zodError) {
  return zodError?.issues?.[0]?.message || "Invalid data.";
}

function SkillsSection() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const { profile, loading: profileLoading, error: profileError, updateProfile } = useProfile(applicantId);

  const [localSkills, setSkills] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [newSkill, setNewSkill] = useState("");
  const [saving, setSaving] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    if (profile?.skills) setSkills([...profile.skills]);
  }, [profile]);

  const handleAddSkill = () => {
    const v = newSkill.trim();
    if (!v) return;

    setSkills((prev) => {
      const exists = prev.some((x) => x.toLowerCase() === v.toLowerCase());
      return exists ? prev : [...prev, v];
    });
    setNewSkill("");
  };

  const handleDelete = (index) => {
    setSkills(localSkills.filter((_, i) => i !== index));
  };

  const handleSave = async () => {
    setErrorMsg("");

    const parsed = updateSkillsSchema.safeParse({ skills: localSkills });
    if (!parsed.success) {
      setErrorMsg(firstZodMessage(parsed.error));
      return;
    }

    setSaving(true);
    try {
      await updateProfile(parsed.data);
      setIsEditing(false);
    } catch (err) {
      setErrorMsg(err?.message || "Failed to save skills.");
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setSkills(profile?.skills || []);
    setIsEditing(false);
    setNewSkill("");
    setErrorMsg("");
  };

  if (profileLoading) return <p className="text-center py-6">Loading skills....</p>;
  if (profileError) return <p className="text-center py-6-red-600">Error: {profileError.message} </p>;

  return (
    <SectionWrapper
      title="Skills"
      onEdit={() => setIsEditing(true)}
      onAdd={() => setIsEditing(true)}
      showEditButtons={!isEditing}
    >
      {errorMsg && (
        <div className="mb-3 p-3 border-2 border-red-600 bg-red-50 font-bold">
          {errorMsg}
        </div>
      )}

      {isEditing ? (
        <div className="space-y-4">
          <div className="flex flex-wrap gap-3">
            {localSkills.map((skill, index) => (
              <div key={index} className="flex items-center gap-2">
                <span
                  className="
                    px-4 py-2 border-2 border-black font-bold rounded-md
                    bg-white text-black
                    hover:bg-primary hover:text-white
                    transition-one
                  "
                >
                  {skill}
                </span>
                <button
                  type="button"
                  onClick={() => handleDelete(index)}
                  className="text-red-600 hover:text-red-800 font-bold"
                >
                  x
                </button>
              </div>
            ))}
          </div>

          <div className="flex gap-2">
            <input
              type="text"
              value={newSkill}
              onChange={(e) => setNewSkill(e.target.value)}
              placeholder="Add new Skill"
              className="px-4 py-2 border-2 border-black rounded-md flex-1"
            />

            <button
              type="button"
              onClick={handleAddSkill}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
            >
              Add
            </button>
          </div>

          <div className="flex gap-4 mt-4">
            <button
              type="button"
              onClick={handleSave}
              disabled={saving}
              className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:opacity-50"
            >
              {saving ? "Saving..." : "Save"}
            </button>
            <button
              type="button"
              onClick={handleCancel}
              className="px-4 py-2 bg-gray-300 rounded-md hover:bg-gray-400"
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
                  px-4 py-2 border-2 border-black font-bold rounded-md
                  bg-white text-black
                  hover:bg-primary hover:text-white
                  transition-none
                "
              >
                {skill}
              </span>
            ))
          ) : (
            <p className="text-gray-500">No skills added yet.</p>
          )}
        </div>
      )}
    </SectionWrapper>
  );
}

export default SkillsSection;
