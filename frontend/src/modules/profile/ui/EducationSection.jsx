import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import {useState, useEffect} from "react";
import {useProfile} from "../hooks/useProfile.js";
import {useSelector} from "react-redux";
function EducationSection() {

  // const applicantId = "86209834-9da5-4c8c-8b9a-ba4073850dba";
  const {user} = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const{profile, loading: profileLoading, error: profileError, updateProfile} = useProfile(applicantId);

  const [educations, setEducations] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [saving, setSaving] = useState(false);

  //Sync education when profile load
  useEffect(() => {
    if(profile?.educations){
      setEducations(profile.educations.map(edu => ({
        educationId: edu.educationId || null,
        applicantId: edu.applicantId || applicantId,
        institution: edu.institution || '',
        degree : edu.degree || '',
        fromYear : edu.fromYear || '',
        toYear : edu.toYear || '',
        gpa : edu.gpa || '',
      })));
    }
  }, [profile, applicantId]);

  const handleAdd = () => {
    setEducations([
        ...educations,
      {
        educationId: null,
        institution: '',
        degree : '',
        fromYear: '',
        toYear: '',
        gpa: '',
      },
    ]);
    setIsEditing(true);
  };

  const handleChange = (index, field, value) => {
    const updated = [...educations];
    updated[index] = {...updated[index], [field] : value};
    setEducations(updated);
  };

  const handleDelete = async (index) => {
    if(window.confirm('Delete this education ?')){
      try{
      //Remove locally
      const updatedEducation = educations.filter((_,i) => i !== index);

      //save to backend
      await updateProfile({educations: updatedEducation});
      alert('Delete successfully!');
      } catch (err){
        alert('Cant delete this education: ' + err.message);
      }
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      //Send only education field, backend accept partial updates
      await updateProfile({educations});
      setIsEditing(false);
      alert('Education saved successfully!');
    } catch (err){
      alert('Failed to save: ' + err.message);
    }finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setEducations(profile?.educations || []);
    setIsEditing(false);
  };

  if(profileLoading) return <p className="text-center py-6">Loading Education....</p>;
  if(profileError) return <p className="text-center py-6">Error: {profileError.message}</p>;

  return (
      <SectionWrapper
        title= "Education"
        onEdit={() => setIsEditing(true)}
        onAdd={handleAdd}
        showEditButtons = {!isEditing} //optional - customize if needed
  >
        <div className = "space-y-6">
          {isEditing ? (
              <>
                {educations.map((edu, index) => (
                    <div key={index} className= "border p-4 rounded-lg bg-gray-50 space-y-3">
                      <input
                      type="text"
                      placeholder="Institution"
                      value={edu.institution}
                      onChange={(e) => handleChange(index,'institution', e.target.value)}
                      className="w-full p-2 border rounded"
                      />
                      <input
                      type="text"
                      placeholder="Degree"
                      value={edu.degree}
                      onChange={(e) => handleChange(index,'degree', e.target.value)}
                      className="w-full p-2 border rounded"
                      />

                      <div className="grid grid-cols-2 gap-4">
                        <input
                        type="number"
                        placeholder="From Year"
                        value={edu.fromYear}
                        onChange={(e) => handleChange(index,'fromYear', e.target.value)}
                        className="w-full p-2 border rounded"
                        />

                        <input
                        type="number"
                        placeholder="To Year"
                        value={edu.toYear}
                        onChange={(e) => handleChange(index,'toYear', e.target.value)}
                        className="w-full p-2 border rounded"
                        />

                        </div>
                        <input
                        type="number"
                        placeholder= "GPA (optional)"
                        value={edu.gpa}
                        onChange={(e) => handleChange(index,'gpa', e.target.value)}
                        className="w-full p-2 border rounded"
                        />

                      <button
                        onClick={() => handleDelete(index)}
                        className="text-red-600 hover:underline"
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
                    className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
                    >
                    Cancel
                  </button>
                  </div>
              </>
          ) : (
              <>
          {educations.length > 0 ? (
              educations.map((edu, index) => (
                  <div key={index}>
              <h4 className="font-black">{edu.institution}</h4>
              <p className="font-bold">{edu.degree}</p>
              <p className="text-sm text-gray-600">
                {edu.fromYear} - {edu.toYear || 'Present'}
              </p>
                    {edu.gpa && <p className="text-sm"> GPA: {edu.gpa} </p> }

                    {index < educations.length - 1 && (<hr className="my-6 border-t border-gray-300"/>)}
                  </div>
            ))

            ) : (
                <p className="text-gray-500">No education entries yet.</p>
          )}
          </>
            )}
        </div>
      </SectionWrapper>
  );

  // return (
  //   <SectionWrapper
  //     title="Education"
  //     onEdit={() => console.log("edit education")}
  //     onAdd={() => console.log("add education")}
  //   >
  //     <div className="space-y-6">
  //
  //       <div>
  //         <h4 className="font-black">RMIT University Vietnam</h4>
  //         <p className="font-bold">Bachelor of Software Engineering (Honours)</p>
  //         <p className="text-sm text-gray-600">2021 – 2025</p>
  //         <p className="text-sm">Media Club · Fintech Club · NXC Culture</p>
  //       </div>
  //
  //       <div>
  //         <h4 className="font-black">American International School</h4>
  //         <p className="font-bold">High School Diploma</p>
  //         <p className="text-sm text-gray-600">2018 – 2021</p>
  //       </div>
  //
  //     </div>
  //   </SectionWrapper>
  // );
}

export default EducationSection;
