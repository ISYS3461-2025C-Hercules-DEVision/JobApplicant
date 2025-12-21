// src/ui/ProfileHeader.jsx
import { useState, useRef } from 'react';
import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import { useProfile } from "../hooks/useProfile.js";
import { profileService } from "../services/profileService.js"; // Import service

function ProfileHeader() {
  const applicantId = "ef23f942-8a9c-46bb-a68e-ee140b2720c1"; // Your real ID
  const { profile, loading, error, updateProfile } = useProfile(applicantId);

  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState(null);

  // Hidden file input ref
  const fileInputRef = useRef(null);

  // Trigger file input click
  const handleAvatarClick = () => {
    fileInputRef.current.click();
  };

  // Handle file selection and upload
  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setUploading(true);
    setUploadError(null);

    try {
      const updatedProfile = await profileService.uploadAvatar(applicantId, file);
      // Update local profile state with new image URL
      await updateProfile({profileImageUrl: updatedProfile.profileImageUrl});
      alert('Avatar uploaded successfully!');
    } catch (err) {
      setUploadError(err.message || 'Failed to upload avatar');
    } finally {
      setUploading(false);
    }
  };

  if (loading) return <p className="text-center py-6">Loading profile...</p>;
  if (error) return <p className="text-center py-6 text-red-600">Error loading profile</p>;
  if (!profile) return <p className="text-center py-6">No profile found</p>;

  const name = profile.fullName || 'Unknown';
  const skills = profile.skills?.join(', ') || 'No skills added';
  const location = `${profile.city || ''}, ${profile.country || ''}`.trim() || 'Location not set';

  return (
      <SectionWrapper
          onEdit={() => console.log("edit profile header")}
          className="p-0"
      >
        <div className="relative">
          {/* Cover Image (optional) */}
          {/* <img ... /> */}

          {/* Avatar + Name */}
          <div className="p-6 flex gap-6 items-center">
            <div className="relative cursor-pointer group" onClick={handleAvatarClick}>
              <img
                  src={profile.profileImageUrl || "https://i.pravatar.cc/160"}
                  alt="Avatar"
                  className="w-28 h-28 rounded-full border-4 border-black object-cover"
              />
              <div className="absolute inset-0 bg-black bg-opacity-50 rounded-full flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
                <span className="text-white text-sm font-bold">Upload</span>
              </div>
            </div>

            {/* Hidden file input */}
            <input
                type="file"
                accept="image/*"
                ref={fileInputRef}
                onChange={handleFileChange}
                className="hidden"
            />

            {uploading && <p className="text-blue-600">Uploading...</p>}
            {uploadError && <p className="text-red-600">{uploadError}</p>}

            <div>
              <h1 className="text-3xl font-black">{name}</h1>
              <p className="font-bold">
                Full-Stack Developer | {skills}
              </p>
              <p className="text-sm text-gray-600">{location}</p>
            </div>
          </div>
        </div>
      </SectionWrapper>
  );

  // return (
  //   <SectionWrapper
  //     onEdit={() => console.log("edit profile header")}
  //     className="p-0"
  //   >
  //     <div className="relative">
  //
  //       {/* Cover Image */}
  //       {/* <img
  //         src="https://picsum.photos/1200/300"
  //         alt="Cover"
  //         className="w-full h-48 object-cover border-b-4 border-black"
  //       /> */}
  //
  //       {/* Avatar + Name */}
  //       <div className="p-6 flex gap-6 items-center">
  //         <img
  //           src="https://i.pravatar.cc/160"
  //           alt="Avatar"
  //           className="w-28 h-28 rounded-full border-4 border-black"
  //         />
  //
  //         <div>
  //           <h1 className="text-3xl font-black">Nguyen Van A</h1>
  //           <p className="font-bold">
  //             Full-Stack Developer | Python, Java, Swift, React
  //           </p>
  //           <p className="text-sm text-gray-600">Ho Chi Minh City, Vietnam</p>
  //         </div>
  //       </div>
  //
  //     </div>
  //   </SectionWrapper>
  // );
}

export default ProfileHeader;
