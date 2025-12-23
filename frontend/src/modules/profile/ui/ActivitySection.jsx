import { useState } from "react";
import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import {useMediaPortfolio} from "../hooks/useMediaPortfolio.js";

function ActivitySection() {
  const applicantId = "ef23f942-8a9c-46bb-a68e-ee140b2720c1";
  const {mediaItems, loading, error, uploading, uploadMedia, deleteMedia} = useMediaPortfolio(applicantId);

  const [showUploadForm, setShowUploadForm] = useState(false);
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
      setShowUploadForm(false);
    } catch (err){
      alert('Upload failed: ' + err.message);
    }
  };

  const handleDelete = async (mediaId) => {
    if(window.confirm('Delete this media item ?')){
      try {
        await deleteMedia(mediaId);
        alert('Media deleted successfully');
      } catch (err){
        alert('Delete failed: ' + err.message);
      }
    }
  };

  const handleCancel = () => {
    setShowUploadForm(false);
    setSelectedFile(null);
    setTitle('');
    setDescription('');
    setVisibility('PRIVATE');
  }

  if(loading) return <p className="text-center py-6">Loading Portfolio....</p>;
  if(error) return  <p className="text-center py-6 text-red-600">Error: {error.message}</p>;

  return (
      <SectionWrapper title="Activity" onAdd={() => setShowUploadForm(true)}>
        <div className="space-y-8">
          {/*Upload Form*/}
          {showUploadForm &&(
          <div className="border-2 border-dashed border-gray-400 rounded-lg p-6 bg-gray-50 space-y-4">
            <h3 className="font-bold text-lg">Add New Media</h3>
            <input type="file"
            accept="image/*,video/*"
            onChange={handleChange}
            className="block w-full text-gray-600 file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
            />
            <input type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Title"
            className="w-full p-3 border-2 border-gray-300 rounded-md focus:border-blue-500"/>

            <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Description"
            className="w-full p-3 border-2 border-gray-300 rounded-md focus:border-blue-500 h-32"/>

            <select
              value={visibility}
              onChange={(e) => setVisibility(e.target.value)}
              className="w-full p-3 border-2 border-gray-300 rounded-md focus:border-blue-500"
              >
              <option value="PUBLIC">Public</option>
              <option value="PRIVATE">Private</option>
            </select>

            <div className="flex-gap-4">
              <button onClick={handleUpload}
                      disabled={uploading || !selectedFile}
                      className="px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 font-semibold">
                {uploading ? 'Uploading...' : 'Upload Media'}
              </button>

              <button onClick={handleCancel}
                      className="px-6 py-3 bg-gray-300 rounded-md hover:bg-gray-400 font-semibold">
                Cancel
              </button>
          </div>
          </div>
          )}

          {/*Portfolio Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {mediaItems.length > 0 ? (
                mediaItems.map((item) => (
                    <div key={item.mediaId} className="border-2 border-black rounded-lg overflow-hidden bg-white shadow-[4px_4px_0px_0px_rgba(0,0,0,1]">
                      {item.mediaType === 'VIDEO' ? (
                          <video src={item.fileUrl}
                                 controls
                                 className="w-full h-64 object-cover"/>
                      ) : (
                          <img src={item.fileUrl}
                          alt={item.title}
                          className="w-full h-64 object-cover"/>
                      )}

                      <div className="p-4 space-y-2">
                        <h4 className="font-black text-lg"> {item.title || 'Untitled'} </h4>
                        <p className="text-gray-700 text-sm"> {item.description || 'No Description'} </p>
                        <p className="text-xs text-gray-500"> Visibility: {item.visibility} </p>

                        {/*Delete button */}
                        <button
                          onClick={() => handleDelete(item.mediaId)}
                          className="mt-3 text-red-600 font-bold hover:underline">
                          Delete
                        </button>
                      </div>
                    </div>
                ))
            ) : (
                <p className="col-span-full text-center text-gray-500 py-12">
                  No media in your portfolio yet. Click the + button to add
                </p>
            )}
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
