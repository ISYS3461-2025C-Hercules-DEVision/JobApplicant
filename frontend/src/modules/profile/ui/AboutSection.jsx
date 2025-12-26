import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import {useProfile} from "../hooks/useProfile.js";
import {useEffect, useState} from "react";

function AboutSection() {

  const applicantId = "86209834-9da5-4c8c-8b9a-ba4073850dba";
  const{profile, loading: profileLoading, error: profileError, updateProfile} = useProfile(applicantId);

  const [objectiveSummary, setObjectiveSummary] = useState('');
  const [isEditing, setIsEditing] = useState(false);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if(profile?.objectiveSummary){
        setObjectiveSummary(profile.objectiveSummary);
    }
  }, [profile]);

  //SAVE
  const handleSave = async () =>{
    setSaving(true);
    try{
      await updateProfile({objectiveSummary: objectiveSummary});
      setIsEditing(false);
      alert('About section updated successfully');
    }catch(err){
      alert('Failed to save: '+ err.message);
    }finally {
      setSaving(false);
    }
  }

  //CANCEL
  const handleCancel = async () => {
    setObjectiveSummary(profile?.objectiveSummary || '');
    setIsEditing(false);
  };

  if(profileLoading) return <p className="text-center py-6">Loading About....</p>;
  if(profileError) return <p className="text-center py-6">Error: {profileError.message}</p>;

  return (
      <SectionWrapper title="About" onEdit={() => setIsEditing(true)}
      >
        {isEditing ? (
            <div className="space-y-4">
              <textarea value={objectiveSummary}
                        onChange={(e) => setObjectiveSummary(e.target.value)}
                        placeholder="Share about your background...."
                        className="w-full p-4 border-2 border-black rounded-md font-bold leading-relaxed resize-y-min-h-[120px]"
                        />
              <div className="flex gap-4">
                   <button onClick={handleSave}
                           disabled={saving}
                           className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
                           >
                     {saving ? 'Saving...' : 'Save'}
                   </button>
                <button
                  onClick={handleCancel}
                  className="px-4 py-2 bg-gray-300 rounded-md hover:bg-gray-400"
                  >
                  Cancel
                </button>
            </div>
            </div>
          ):(
              <p className="font-bold leading-relaxed">
                {objectiveSummary || 'No about information added yet.'}
              </p>
        )}
      </SectionWrapper>
  );
  // return (
  //   <SectionWrapper
  //     title="About"
  //     onEdit={() => console.log("edit about")}
  //   >
  //     <p className="font-bold leading-relaxed">
  //       I am a Software Engineering student with a passion for frontend
  //       development, UI/UX design, and modern JavaScript engineering.
  //     </p>
  //   </SectionWrapper>
  // );
};

export default AboutSection;
