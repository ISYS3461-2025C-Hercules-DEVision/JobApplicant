import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";

function ProfileHeader() {
  return (
    <SectionWrapper 
      onEdit={() => console.log("edit profile header")} 
      className="p-0"
    >
      <div className="relative">

        {/* Cover Image */}
        {/* <img
          src="https://picsum.photos/1200/300"
          alt="Cover"
          className="w-full h-48 object-cover border-b-4 border-black"
        /> */}

        {/* Avatar + Name */}
        <div className="p-6 flex gap-6 items-center">
          <img
            src="https://i.pravatar.cc/160"
            alt="Avatar"
            className="w-28 h-28 rounded-full border-4 border-black"
          />

          <div>
            <h1 className="text-3xl font-black">Nguyen Van A</h1>
            <p className="font-bold">
              Full-Stack Developer | Python, Java, Swift, React
            </p>
            <p className="text-sm text-gray-600">Ho Chi Minh City, Vietnam</p>
          </div>
        </div>

      </div>
    </SectionWrapper>
  );
}

export default ProfileHeader;
