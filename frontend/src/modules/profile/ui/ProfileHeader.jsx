// src/modules/profile/ui/ProfileHeader.jsx
import { useState, useRef, useEffect } from "react";
import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import { useProfile } from "../hooks/useProfile.js";
import { profileService } from "../services/profileService.js"; // Import service
import { subscriptionService } from "../../subscription/services/subscriptionService";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";

function ProfileHeader() {
  // const applicantId = "86209834-9da5-4c8c-8b9a-ba4073850dba"; // Your real ID
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const { profile, loading, error, updateProfile } = useProfile(applicantId);

  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState(null);

  const [isEditingLocation, setIsEditingLocation] = useState(false);
  const [localCity, setLocalCity] = useState("");
  const [localCountry, setLocalCountry] = useState("");
  const [savingLocation, setSavingLocation] = useState(false);

  const [subscription, setSubscription] = useState(null);
  const navigate = useNavigate();

  // Hidden file input ref
  const fileInputRef = useRef(null);

  // Sync location when profile loads
  useEffect(() => {
    if (profile) {
      setLocalCity(profile.city || "");
      setLocalCountry(profile.country || "");
    }
  }, [profile]);

  // Check subscription status
  useEffect(() => {
    if (!applicantId) return;

    subscriptionService
      .getMySubscription(applicantId)
      .then(setSubscription)
      .catch(console.error);
  }, [applicantId]);

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
      const updatedProfile = await profileService.uploadAvatar(
        applicantId,
        file
      );
      await updateProfile({ profileImageUrl: updatedProfile.profileImageUrl });
      alert("Avatar uploaded successfully!");
    } catch (err) {
      setUploadError(err.message || "Failed to upload avatar");
    } finally {
      setUploading(false);
      e.target.value = null; // reset input
    }
  };

  // Save city and country
  const handleSaveLocation = async () => {
    setSavingLocation(true);
    try {
      await updateProfile({
        city: localCity.trim(),
        country: localCountry.trim(),
      });
      setIsEditingLocation(false);
      alert("Location updated!");
    } catch (err) {
      alert("Failed to update location: " + err.message);
    } finally {
      setSavingLocation(false);
    }
  };

  const handleCancelUpdateLocation = () => {
    setLocalCity(profile?.city || "");
    setLocalCountry(profile?.country || "");
    setIsEditingLocation(false);
  };

  if (loading) return <p className="p-12 text-center">Loading profile...</p>;
  if (error)
    return (
      <p className="text-center p-12 text-red-600">Error loading profile</p>
    );
  if (!profile) return <p className="text-center p-12">No profile found</p>;

  const name = profile.fullName || "Unknown";
  const skills = profile.skills?.join(", ") || "No skills added";
  const location = `${localCity || "City"}, ${
    localCountry || "Country"
  }`.trim();

  const planLabel =
    subscription?.active && subscription?.planType === "PREMIUM"
      ? "PREMIUM"
      : "FREE";

  return (
    <SectionWrapper className="p-0">
      <div className="px-8 py-12 bg-white">
        <div className="flex flex-col md:flex-row gap-8 items-start max-w-5xl mx-auto">
          {/* Avatar */}
          <div
            className="relative group cursor-pointer flex-shrink-0"
            onClick={handleAvatarClick}
          >
            <div className="w-40 h-40 rounded-full overflow-hidden border-8 border-white shadow-2xl bg-gray-200">
              <img
                src={profile.profileImageUrl || "https://i.pravatar.cc/300"}
                alt="Avatar"
                className="w-full h-full object-cover"
              />
            </div>

            <div className="absolute inset-0 rounded-full bg-black bg-opacity-40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity">
              <span className="text-white font-bold">Change</span>
            </div>

            <input
              type="file"
              accept="image/*"
              ref={fileInputRef}
              onChange={handleFileChange}
              className="hidden"
            />
          </div>

          {/* Info */}
          <div className="flex-1 mt-4 md:mt-0">
            <h1 className="text-5xl font-black text-gray-900 leading-tight">
              {name}
            </h1>

            <p className="text-xl font-medium text-gray-700 mt-2">
              Full Stack Developer | {skills}
            </p>

            {/* Subscription status button */}
            <button
              onClick={() => navigate("/subscription")}
              className="
                mt-4 px-4 py-2
                border-2 border-black
                font-bold rounded-md
                bg-white text-black
                hover:bg-primary hover:text-white
                transition-all duration-200
              "
            >
              {planLabel}
            </button>

            {/* Location/editable */}
            <div className="mt-6 flex items-center gap-4">
              {isEditingLocation ? (
                <div className="flex flex-col sm:flex-row gap-3 items-end">
                  <div className="flex-1">
                    <label className="block text-sm font-bold mb-1">City</label>
                    <input
                      type="text"
                      value={localCity}
                      onChange={(e) => setLocalCity(e.target.value)}
                      className="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:border-blue-500 focus:outline-none"
                      placeholder="City"
                    />
                  </div>

                  <div className="flex-1">
                    <label className="block text-sm font-bold mb-1">
                      Country
                    </label>
                    <input
                      type="text"
                      value={localCountry}
                      onChange={(e) => setLocalCountry(e.target.value)}
                      className="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:border-blue-500 focus:outline-none"
                      placeholder="Country"
                    />
                  </div>

                  <div className="flex gap-2">
                    <button
                      onClick={handleSaveLocation}
                      disabled={savingLocation}
                      className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
                    >
                      {savingLocation ? "Saving..." : "Save"}
                    </button>

                    <button
                      onClick={handleCancelUpdateLocation}
                      className="px-5 py-2 bg-gray-300 rounded-lg hover:bg-gray-400"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <div className="flex items-center gap-3">
                  <p className="text-lg text-gray-700">
                    {location === "," ? "Location not set" : location}
                  </p>
                  <button
                    onClick={() => setIsEditingLocation(true)}
                    className="text-blue-600 hover:underline font-medium"
                  >
                    Edit
                  </button>
                </div>
              )}
            </div>

            {/* Upload status */}
            {uploading && (
              <p className="text-blue-600 mt-2">Uploading Avatar...</p>
            )}
            {uploadError && <p className="text-red-600 mt-2">{uploadError}</p>}
          </div>
        </div>
      </div>
    </SectionWrapper>
  );
}

export default ProfileHeader;
