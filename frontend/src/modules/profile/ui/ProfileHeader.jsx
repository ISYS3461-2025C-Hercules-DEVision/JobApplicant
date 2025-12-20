import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import {useProfile} from "../hooks/useProfile.js";

function ProfileHeader() {

  //Get applicantId from local storage (set during login)
  const applicantId = "2c79ba28-b646-4426-b140-284f448b3da4";
  const {profile, loading, error} = useProfile(applicantId);

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
          {/* <img
          src="https://picsum.photos/1200/300"
          alt="Cover"
          className="w-full h-48 object-cover border-b-4 border-black"
        /> */}

          {/* Avatar + Name */}
          <div className="p-6 flex gap-6 items-center">
            <img
                src={profile.profileImageUrl || "https://i.pravatar.cc/160"}
                alt="Avatar"
                className="w-28 h-28 rounded-full border-4 border-black"
            />

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
