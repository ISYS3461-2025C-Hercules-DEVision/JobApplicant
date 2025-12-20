import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import {useExperience} from "../hooks/useExperience.js";
import {useEffect, useState} from "react";
function ExperienceSection({applicantId}) {

  const {experiences, loading, error, updateExperiences} = useExperience(applicantId);

  const [localExperiences, setLocalExperiences] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [saving, setSaving] = useState(false);

  //sync local state when data loads
  useEffect(() => {
    if(experiences) {
      setLocalExperiences(experiences.map(exp => ({...exp})));
    }
  }, [experiences]);

  //add new experience
  const addExperience = () => {
    setLocalExperiences([
        ...localExperiences,
      {
        workExpId: null,
        applicantId,
        jobTitle: '',
        companyName: '',
        fromYear: '',
        toYear: '',
        description: '',
      },
    ]);
    setIsEditing(true);
  };

  //Update a field
  const handleChange = (index, field, value) => {
    const updated = [...localExperiences];
    updated[index] = {...updated[index],[field] : value};
    setLocalExperiences(updated);
  };

  //Delete experience
  const deleteExperience = (index) => {
    if(window.confirm('Delete this experience ? ')){
      setLocalExperiences(localExperiences.filter((_,i) => i !== index));
    }
  };

  //Save changes to backend
  const handleSave = async () => {
    setSaving(true);
    try{
      await updateExperiences(localExperiences);
      setIsEditing(false);
      alert('Experience updated successfully!');
    }catch (err){
      alert('Failed to save: ' + err.message);
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setLocalExperiences(experiences || []);
    setIsEditing(false);
  };

  if(loading) return <p className="text-center py-6">Loading experiences....</p>;
  if(error) return <p className="text-center py-6">Error: {error.message}</p>;


  return (
      <SectionWrapper
        title="Experience"
        onEdit={() => setIsEditing(true)}
        onAdd={addExperience}
        showEditButtons={!isEditing} //Hide button when editting
  >
        <div className="space-y-6">
          {isEditing ? (
              <>
                {localExperiences.map((exp, index) => (
                    <div key={index} className="border p-4 rounded-lg bg-gray-50 space-y-3">
                      <input
                        type="text"
                        placeholder="Job Title"
                        value={exp.jobTitle}
                        onChange={(e) => handleChange(index, 'jobTitle', e.target.value)}
                        className="w-full p-2 border grounded"
                        />
                      <input
                        type="text"
                        placeholder="Company Name"
                        value={exp.companyName}
                        onChange={(e) => handleChange(index, 'companyName', e.target.value)}
                        className="w-full p-2 border grounded"
                      />
                      <div className="grid grid-cols-2 gap-4">
                        <input
                        type="text"
                        placeholder="From Year"
                        value={exp.fromYear}
                        onChange={(e) => handleChange(index, 'fromYear', e.target.value)}
                        className="w-full p-2 border grounded"
                        />

                        <input
                        type="text"
                        placeholder="To Year"
                        value={exp.toYear}
                        onChange={(e) => handleChange(index, 'toYear', e.target.value)}
                        className="w-full p-2 border grounded"
                        />

                      </div>

                      <textarea
                        placeholder="Description"
                        value={exp.description}
                        onChange={(e) => handleChange(index, 'description', e.target.value)}
                        className="w-full p-2 border grounded h-24"
                        />

                      <button
                        onClick={() => deleteExperience(index)}
                        className={"text-red-600 hover:underline"}
                        >
                        Delete
                        </button>
                    </div>
                ))}

                <div className="flex gap-4 mt-4">
                  <button
                    onClick={handleSave}
                    disabled={saving}
                    className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
                    >
                    {saving ? 'Saving...' : 'Save'}
                  </button>

                  <button
                    onClick={handleCancel}
                    className="px-4 py-2 bg-gray-300 rounded hover: bg-gray-400"
                    >
                    Cancel
                  </button>
                  </div>
              </>
          ) : (
              <>
                {localExperiences.length > 0 ? (
                localExperiences.map((exp,index) => (
                <div key={index} className="border-b pb-4">
                  <h4 className="font-bold">{exp.jobTitle}</h4>
                  <p className="text-gray-700">{exp.companyName}</p>
                  <p className="text-sm text-gray-600">
                    {exp.fromYear} - {exp.toYear || 'Present'}
                  </p>
                  <p className="mt-2 text-gray-600">{exp.description}</p>
                  </div>
                ))
                ) : (
                <p className="text-gray-500"> No Experiences added yet.</p>
                )}
              </>
          )}
        </div>
      </SectionWrapper>
  );
}
//   return (
//     <SectionWrapper
//       title="Experience"
//       onEdit={() => console.log("edit experience")}
//       onAdd={() => console.log("add experience")}
//     >
//       <div className="space-y-6">
//
//         <div>
//           <h4 className="font-black">Software Engineer Intern</h4>
//           <p className="font-bold">Netway Technology — Internship</p>
//           <p className="text-sm text-gray-600">Oct 2023 – Feb 2024 · Hybrid</p>
//         </div>
//
//         <div>
//           <h4 className="font-black">Machine Learning Intern</h4>
//           <p className="font-bold">PetroVietnam — Internship</p>
//           <p className="text-sm text-gray-600">Oct 2022 – Mar 2023 · Remote</p>
//         </div>
//
//       </div>
//     </SectionWrapper>
//   );
// }

export default ExperienceSection;
