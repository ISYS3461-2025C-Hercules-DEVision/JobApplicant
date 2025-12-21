import { useState } from "react";
import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import {useMediaPortfolio} from "../hooks/useMediaPortfolio.js";

function ActivitySection() {
  const applicantId = "ef23f942-8a9c-46bb-a68e-ee140b2720c1";
  const {mediaItems, loading, error, uploading, uploadMedia} = useMediaPortfolio(applicantId);

  const [selectedFile, setSelectedFile] = useState(null);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [visibility, setVisibility] = useState('PRIVATE');

  const handleChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if(!selectedFile) return alert('Select a file');
    try {
      await uploadMedia(selectedFile, title, description, visibility);
      alert('Media upload successfully');

      //Clear form
      setSelectedFile(null);
      setTitle('');
      setDescription('');
      setVisibility('PRIVATE');
    } catch (err){
      alert('Upload failed: ' + err.message);
    }
  };

  if(loading) return <p className="text-center py-6">Loading Portfolio....</p>;
  if(error) return  <p className="text-center py-6">Error: {error.message}</p>;

  return (
      <SectionWrapper title="Media Portfolio" onAdd={() => console.log('Add media')}>
        <div className="space-y-6">
          {/*Upload Form*/}
          <div className="space-y-4">
            <input type="file" accept="image/*,video/*" onChange={handleChange} className="block"/>
            <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Title"
            className="w-full p-2 border grounded"/>
            <textarea value = {description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Description"
            className="w-full p-2 border grounded"/>
            <select value={visibility}
            onChange={(e) => setVisibility(e.target.value)}
            className="w-full p-2 border grounded">
              <option value="PRIVATE">Private</option>
              <option value="PUBLIC">Public</option>
            </select>

            <button onClick={handleUpload}
            disabled={uploading || !selectedFile}
            className="px-4 py-2 bg-blue-600 text-white grounded hover:bg-blue-700 disabled:opacity-50">
              {uploading ? 'Uploading...' : 'Upload Media'}
            </button>
          </div>

          {/*Display Portfolio*/}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {mediaItems.map((item) =>(
              <div key={item.id || item.title} className="border rounded-lg overflow-hidden bg-gray-50">
                {item.mediaType === 'VIDEO' ? (
                  <video src={item.fileUrl} controls className="w-full h-48 object-cover"/>
                    ) : (
                        <img src={item.fileUrl} alt={item.title} className="w-full h-48 object-cover"/>
                    )}
                <div className="p-2">
                  <h4 className="font-bold">{item.title}</h4>
                  <p className="text-sm text-gray-600">{item.description}</p>
                  <p className="text-xs text-gray-500 mt-1">Visibility: {item.visibility}</p>

                </div>
              </div>
            ))}
          </div>
        </div>
      </SectionWrapper>
  );

  // return (
  //   <SectionWrapper
  //     title="Activity"
  //     onEdit={() => console.log("edit activity")}
  //     onAdd={() => console.log("add new activity")}
  //   >
  //     <div className="grid md:grid-cols-3 gap-4">
  //       {activities.map((i) => (
  //         <div
  //           key={i}
  //           className="
  //             border-4 border-black bg-white p-3
  //             shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]
  //             hover:translate-x-1 hover:translate-y-1 hover:shadow-none
  //             transition-none
  //           "
  //         >
  //           <img
  //             src={`https://picsum.photos/400?random=${i}`}
  //             className="w-full h-32 object-cover border-2 border-black"
  //           />
  //
  //           <p className="font-black mt-2">Wrapping up our Capstone Journey!</p>
  //           <p className="text-sm text-gray-600">3w ago</p>
  //
  //           <div className="flex items-center gap-4 mt-2 text-sm">
  //             <span>❤️ 1,498</span>
  //             <span>💬 3,000</span>
  //           </div>
  //         </div>
  //       ))}
  //     </div>
  //   </SectionWrapper>
  // );
}

export default ActivitySection;
