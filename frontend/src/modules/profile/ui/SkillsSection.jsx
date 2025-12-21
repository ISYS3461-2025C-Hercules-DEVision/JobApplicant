import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import {useState, useEffect} from "react";
import {useProfile} from "../hooks/useProfile.js";


function SkillsSection() {
  // const [profile, loading, error, updateProfile] = useProfile(applicantId);
  const applicantId = "ef23f942-8a9c-46bb-a68e-ee140b2720c1";
  const {profile, loading: profileLoading, error: profileError, updateProfile} = useProfile(applicantId);

  const [localSkills, setSkills] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [newSkill, setNewSkill] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (profile?.skills) {
      setSkills([...profile.skills]);
    }
  }, [profile]);

  //Add new skill
  const handleAddSkill = () => {
    if (newSkill.trim()) {
      setSkills([...localSkills, newSkill.trim()]);
      setNewSkill('');
    }
  };

  const handleDelete = (index) => {
    setSkills(localSkills.filter((_, i) => i !== index));
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      await updateProfile({skills: localSkills});
      setIsEditing(false);
      alert('Skills updated successfully');
    } catch (err) {
      alert('Failed to save: ' + err.message);
    } finally {
      setSaving(false);
    }
  };

  //Cancel
  const handleCancel = () => {
    setSkills(profile?.skills || []);
    setIsEditing(false);
    setNewSkill('');
  };

  if (profileLoading) return <p className="text-center py-6">Loading skills....</p>;
  if (profileError) return <p className="text-center py-6-red-600">Error: {profileError.message} </p>;

  return (
      <SectionWrapper
          title="Skills" onEdit={() => setIsEditing(true)}
          onAdd={() => setIsEditing(true)} showEditButtons={!isEditing}>
        {isEditing ? (
            <div className="space-y-4">
              <div className="flex flex-wrap gap-3">
                {localSkills.map((skill, index) => (
                    <div key={index} className="flex items-center gap-2">
                      <span className="
                      px-4 py-2 border-2 border-black font-bold rounded-md
                      bg-white text-black
                      hover:bg-primary hover:text-white
                      transition-one">
                        {skill}
                      </span>
                      <button onClick={() => handleDelete(index)}
                              className="text-red-600 hover:text-red-800 font-bold">
                        x
                      </button>
                    </div>
                ))}
              </div>
              <div className="flex gap-2">
                <input type="text" value={newSkill}
                       onChange={(e) => setNewSkill(e.target.value)}
                       placeholder="Add new Skill"
                       className="px-4 py-2 border-2 border-black rounded-md flex-1"/>

                <button onClick={handleAddSkill}
                        className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">
                  Add
                </button>
              </div>

              <div className="flex gap-4 mt-4">
                <button onClick={handleSave}
                        disabled={saving}
                        className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:opacity-50"
                >
                  {saving ? 'Saving...' : 'Save'}
                </button>
                <button onClick={handleCancel}
                        className="px-4 py-2 bg-gray-300 rounded-md hover:bg-gray-400">
                  Cancel
                </button>
              </div>
            </div>
        ) : (
            <div className="flex flex-wrap gap-3">
              {localSkills.length > 0 ? (
                  localSkills.map((skill) => (
                      <span key={skill}
                            className="
                      px-4 py-2 border-2 border-black font-bold rounded-md
                      bg-white text-black
                      hover:bg-primary hover:text-white
                      transition-none">
                        {skill}
                      </span>
                  ))
              ) : (
                  <p className="text-gray-500">No skills added yet.</p>
              )}
            </div>
        )}
      </SectionWrapper>
  )
}
//   return (
//     <SectionWrapper
//       title="Skills"
//       onEdit={() => console.log("edit skills")}
//       onAdd={() => console.log("add skill")}
//     >
//       <div className="flex flex-wrap gap-3">
//         {skills.map((s) => (
//           <span
//             key={s}
//             className="
//               px-4 py-2 border-2 border-black font-bold rounded-md
//               bg-white text-black
//               hover:bg-primary hover:text-white
//               transition-none
//             "
//           >
//             {s}
//           </span>
//         ))}
//       </div>
//     </SectionWrapper>
//   );
// }

export default SkillsSection;
