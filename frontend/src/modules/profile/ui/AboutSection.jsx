// src/modules/profile/ui/AboutSection.jsx
import { useState } from "react";
import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import { useResume } from "../hooks/useResume";
import { useSelector } from "react-redux";

function AboutSection() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const { resume, loading, error, updateResume, isSubmitting } = useResume(applicantId);

  // Only local state needed for editing
  const [isEditing, setIsEditing] = useState(false);
  const [editedObjective, setEditedObjective] = useState("");

  // When entering edit mode → initialize with current value
  const handleEnterEdit = () => {
    setEditedObjective(resume?.objective || "");
    setIsEditing(true);
  };

  // Save
  const handleSave = async () => {
    if (!editedObjective.trim()) {
      alert("Please enter a professional summary.");
      return;
    }

    try {
      await updateResume({
        objective: editedObjective.trim(),
      });
      alert("Professional summary updated!");
      setIsEditing(false);
    } catch (err) {
      alert("Failed to save: " + (err.message || "Unknown error"));
    }
  };

  // Cancel → just exit edit mode (no need to reset, it will re-sync on next render)
  const handleCancel = () => {
    setIsEditing(false);
  };

  // Loading / Error
  if (loading) {
    return (
        <SectionWrapper title="About">
          <p className="text-center py-6 text-gray-600">Loading...</p>
        </SectionWrapper>
    );
  }

  if (error) {
    return (
        <SectionWrapper title="About">
          <p className="text-center py-6 text-red-600">Error: {error}</p>
        </SectionWrapper>
    );
  }

  const objective = resume?.objective || "No professional summary added yet.";

  return (
      <SectionWrapper
          title="About"
          onEdit={handleEnterEdit}
          showEditButtons={!isEditing}
      >
        {isEditing ? (
            <div className="space-y-6">
              <div>
                <label className="block text-lg font-black uppercase mb-2">
                  Professional Summary / Objective
                </label>
                <textarea
                    value={editedObjective}
                    onChange={(e) => setEditedObjective(e.target.value)}
                    placeholder="Tell employers about yourself, your passion, skills, and what you bring..."
                    rows={6}
                    className="w-full px-4 py-3 border-4 border-black font-bold focus:outline-none focus:ring-4 focus:ring-primary placeholder:text-gray-500 resize-none"
                />
              </div>

              <div className="flex gap-4 mt-8">
                <button
                    onClick={handleSave}
                    disabled={isSubmitting}
                    className="px-8 py-4 bg-primary text-white font-black border-4 border-black rounded hover:bg-blue-700 disabled:opacity-50"
                >
                  {isSubmitting ? "Saving..." : "Save Changes"}
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
            <div className="space-y-4">
              <p className="text-gray-700 whitespace-pre-line text-lg leading-relaxed">
                {objective}
              </p>

              {!resume?.objective && (
                  <p className="text-sm text-gray-500 italic mt-4">
                    Click Edit to add your professional summary — this is what employers read first!
                  </p>
              )}
            </div>
        )}
      </SectionWrapper>
  );
}

export default AboutSection;