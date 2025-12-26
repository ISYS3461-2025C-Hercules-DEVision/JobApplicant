import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import {useState, useEffect} from "react";
import {useProfile} from "../hooks/useProfile.js";

function ContactSection() {

  // const {profile, loading, error, updateProfile} = useProfile(applicantId);
  const applicantId = "86209834-9da5-4c8c-8b9a-ba4073850dba";
  const {profile, loading: profileLoading, error: profileError, updateProfile} = useProfile(applicantId);

  const [isEditing, setIsEditing] = useState(false);
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (profile) {
      setPhone(profile.phoneNumber || '');
      setEmail(profile.email || '');
    }
  }, [profile]);

  //Save change
  const handleSave = async () => {
    setSaving(true);
    try {
      await updateProfile({
        phoneNumber: phone,
        email: email,
      });
      setIsEditing(false);
      alert('Contact information updated successfully');
    } catch (err) {
      alert('Fail to save: ' + err.message);
    } finally {
      setSaving(false);
    }
  };

  //Cancel change
  const handleCancel = () => {
    setPhone(profile?.phoneNumber || '');
    setEmail(profile?.email || '');
    setIsEditing(false);
  };

  if (profileLoading) return <p className="text-center py-6">Loading Contact Information...</p>;
  if (profileError) return <p className="text-center py-6">Error: {profileError.message}</p>;

  return (
      <SectionWrapper title="Contact Information" onEdit={() => setIsEditing(true)}>
        {isEditing ? (
            <div className="space-y-4">
              <div>
                <label className="block font-bold mb-1">Email</label>
                <input type="Email" value={email}
                       onChange={(e) => setEmail(e.target.value)}
                       placeholder="Enter email"
                       className="w-full p-2 border-2 border-black rounded-md"/>
              </div>

              <div>
                <label className="block font-bold- mb-1">Phone Number</label>
                <input type="Tel" value={phone}
                       onChange={(e) => setPhone(e.target.value)}
                       placeholder="Enter phone number"
                       className="w-full p-2 border-2 border-black rounded-md"/>
              </div>
              <div className="flex gap-4">
                <button onClick={handleSave} disabled={saving}
                        className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50">
                  {saving ? 'Saving...' : 'Save'}
                </button>
                <button onClick={handleCancel}
                        className="px-4 py-2 bg-gray-300 rounded-md hover:bg-gray-400">
                  Cancel
                </button>
              </div>
            </div>
        ) : (
            <div className="space-y-3 font-bold">
              <p> Email: {profile?.email || 'Not provided'} </p>
              <p> Phone: {profile?.phoneNumber || 'Not provided'} </p>
            </div>
        )}
      </SectionWrapper>
  );
}
//   return (
//     <SectionWrapper
//       title="Contact"
//       onEdit={() => console.log("edit contact")}
//       onAdd={() => console.log("add contact link")}
//     >
//       <div className="space-y-3 font-bold">
//         {links.map((link, i) => (
//           <p key={i}>{link}</p>
//         ))}
//       </div>
//     </SectionWrapper>
//   );
// }

export default ContactSection;
