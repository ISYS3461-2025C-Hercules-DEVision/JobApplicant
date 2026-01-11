import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";
import { useState, useEffect } from "react";
import { useProfile } from "../hooks/useProfile.js";
import { useSelector } from "react-redux";
import { updateContactSchema } from "../../../schemas/profileSchema";

function firstZodMessage(zodError) {
  return zodError?.issues?.[0]?.message || "Invalid data.";
}

function ContactSection() {
  const { user } = useSelector((state) => state.auth);
  const applicantId = user?.applicantId;

  const { profile, loading: profileLoading, error: profileError, updateProfile } = useProfile(applicantId);

  const [isEditing, setIsEditing] = useState(false);
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [saving, setSaving] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    if (profile) {
      setPhone(profile.phoneNumber || "");
      setEmail(profile.email || "");
    }
  }, [profile]);

  const handleSave = async () => {
    setErrorMsg("");

    const parsed = updateContactSchema.safeParse({
      phoneNumber: phone,
      email,
    });

    if (!parsed.success) {
      setErrorMsg(firstZodMessage(parsed.error));
      return;
    }

    setSaving(true);
    try {
      await updateProfile(parsed.data);
      setIsEditing(false);
    } catch (err) {
      setErrorMsg(err?.message || "Failed to update contact.");
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setPhone(profile?.phoneNumber || "");
    setEmail(profile?.email || "");
    setIsEditing(false);
    setErrorMsg("");
  };

  if (profileLoading) return <p className="text-center py-6">Loading Contact Information...</p>;
  if (profileError) return <p className="text-center py-6">Error: {profileError.message}</p>;

  return (
    <SectionWrapper title="Contact Information" onEdit={() => setIsEditing(true)}>
      {errorMsg && (
        <div className="mb-3 p-3 border-2 border-red-600 bg-red-50 font-bold">
          {errorMsg}
        </div>
      )}

      {isEditing ? (
        <div className="space-y-4">
          <div>
            <label className="block font-bold mb-1">Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter email"
              className="w-full p-2 border-2 border-black rounded-md"
            />
          </div>

          <div>
            <label className="block font-bold mb-1">Phone Number</label>
            <input
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="Enter phone number"
              className="w-full p-2 border-2 border-black rounded-md"
            />
          </div>

          <div className="flex gap-4">
            <button
              type="button"
              onClick={handleSave}
              disabled={saving}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50"
            >
              {saving ? "Saving..." : "Save"}
            </button>
            <button
              type="button"
              onClick={handleCancel}
              className="px-4 py-2 bg-gray-300 rounded-md hover:bg-gray-400"
            >
              Cancel
            </button>
          </div>
        </div>
      ) : (
        <div className="space-y-3 font-bold">
          <p>Email: {profile?.email || "Not provided"}</p>
          <p>Phone: {profile?.phoneNumber || "Not provided"}</p>
        </div>
      )}
    </SectionWrapper>
  );
}

export default ContactSection;
