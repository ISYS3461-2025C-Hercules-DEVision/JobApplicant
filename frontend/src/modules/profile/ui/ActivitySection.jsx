// src/modules/profile/ui/MediaPortfolioSection.jsx
import { useState } from "react";
import { useSelector } from "react-redux";
import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import { useResume } from "../hooks/useResume";
import {useMediaPortfolio} from "../hooks/useMediaPortfolio.js";

function ActivitySection() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const { resume, loading: resumeLoading, error: resumeError } = useResume(applicantId);

  // Get resumeId from fetched resume
  const resumeId = resume?.resumeId;

  // Use media hook with resumeId
  const {
    mediaItems,
    loading: mediaLoading,
    error: mediaError,
    uploading,           // ← now defined
    uploadMedia,
    deleteMedia,
  } = useMediaPortfolio(resumeId);

  const [showUploadForm, setShowUploadForm] = useState(false);
  const [selectedFile, setSelectedFile] = useState(null);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [visibility, setVisibility] = useState("PRIVATE");

  // Loading / Error states
  if (resumeLoading || mediaLoading) {
    return (
        <SectionWrapper title="Media Portfolio">
          <p className="text-center py-6 text-gray-600">Loading portfolio...</p>
        </SectionWrapper>
    );
  }

  if (resumeError || mediaError) {
    return (
        <SectionWrapper title="Media Portfolio">
          <p className="text-center py-6 text-red-600">
            Error: {resumeError || mediaError || "Unknown error"}
          </p>
        </SectionWrapper>
    );
  }

  if (!resumeId) {
    return (
        <SectionWrapper title="Media Portfolio">
          <p className="text-center py-6 text-gray-600">
            No resume found. Please create a resume first.
          </p>
        </SectionWrapper>
    );
  }

  // File selection
  const handleFileChange = (e) => {
    setSelectedFile(e.target.files?.[0] || null);
  };

  // Upload handler
  const handleUpload = async () => {
    if (!selectedFile) {
      alert("Please select a file first.");
      return;
    }

    try {
      await uploadMedia(selectedFile, title, description, visibility);
      alert("Media uploaded successfully!");

      // Reset form
      setSelectedFile(null);
      setTitle('');
      setDescription('');
      setVisibility('PRIVATE');
      setShowUploadForm(false);
    } catch (err) {
      alert("Upload failed: " + err.message);
    }
  };

  // Cancel modal
  const handleCancel = () => {
    setShowUploadForm(false);
    setSelectedFile(null);
    setTitle("");
    setDescription("");
    setVisibility("PRIVATE");
  };

  // Delete media
  const handleDelete = async (mediaId) => {
    if (!window.confirm("Delete this media item?")) return;

    try {
      await deleteMedia(mediaId);
      alert("Media deleted successfully");
    } catch (err) {
      alert("Delete failed: " + err.message);
    }
  };

  return (
      <SectionWrapper title="Media Portfolio" onAdd={() => setShowUploadForm(true)}>
        <div className="space-y-8">
          {/* Upload Form */}
          {showUploadForm && (
              <div className="border-2 border-dashed border-gray-400 rounded-lg p-6 bg-gray-50 space-y-4">
                <h3 className="font-bold text-lg">Add New Media</h3>

                <input
                    type="file"
                    accept="image/*,video/*"
                    onChange={handleFileChange}
                    className="block w-full text-gray-600 file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
                />

                <input
                    type="text"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="Title"
                    className="w-full p-3 border-2 border-gray-300 rounded-md focus:border-blue-500"
                />

                <textarea
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    placeholder="Description"
                    className="w-full p-3 border-2 border-gray-300 rounded-md focus:border-blue-500 h-32"
                />

                <select
                    value={visibility}
                    onChange={(e) => setVisibility(e.target.value)}
                    className="w-full p-3 border-2 border-gray-300 rounded-md focus:border-blue-500"
                >
                  <option value="PUBLIC">Public</option>
                  <option value="PRIVATE">Private</option>
                </select>

                <div className="flex gap-4">
                  <button
                      onClick={handleUpload}
                      disabled={uploading || !selectedFile}
                      className="px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 font-semibold"
                  >
                    {uploading ? "Uploading..." : "Upload Media"}
                  </button>

                  <button
                      onClick={handleCancel}
                      className="px-6 py-3 bg-gray-300 rounded-md hover:bg-gray-400 font-semibold"
                  >
                    Cancel
                  </button>
                </div>
              </div>
          )}

          {/* Portfolio Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {mediaItems.length > 0 ? (
                mediaItems.map((item) => (
                    <div
                        key={item.mediaId}
                        className="border-2 border-black rounded-lg overflow-hidden bg-white shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
                    >
                      {item.mediaType === "VIDEO" ? (
                          <video src={item.fileUrl} controls className="w-full h-64 object-cover" />
                      ) : (
                          <img src={item.fileUrl} alt={item.title} className="w-full h-64 object-cover" />
                      )}

                      <div className="p-4 space-y-2">
                        <h4 className="font-black text-lg">{item.title || "Untitled"}</h4>
                        <p className="text-gray-700 text-sm">{item.description || "No Description"}</p>
                        <p className="text-xs text-gray-500">Visibility: {item.visibility}</p>

                        <button
                            onClick={() => handleDelete(item.mediaId)}
                            className="mt-3 text-red-600 font-bold hover:underline"
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                ))
            ) : (
                <p className="col-span-full text-center text-gray-500 py-12">
                  No media in your portfolio yet. Click the + button to add.
                </p>
            )}
          </div>
        </div>
      </SectionWrapper>
  );
}

export default ActivitySection;