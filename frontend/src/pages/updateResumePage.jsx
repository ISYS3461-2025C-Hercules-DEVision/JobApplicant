// import React, { useMemo, useState } from "react";
//
// /**
//  * Props:
//  * - initialResume (optional): Resume object to prefill fields
//  * - applicantId: string
//  * - resumeId: string
//  * - onClose: () => void
//  * - onSubmitResume: async (payload) => Promise<void>
//  * - onUploadMedia: async ({file, title, description, visibility}) => Promise<MediaPortfolio>
//  * - onRemoveMedia: async (mediaIdOrUrl) => Promise<void> (optional)
//  */
// export default function UpdateResumePage({
//                                              initialResume,
//                                              applicantId,
//                                              resumeId,
//                                              onClose,
//                                              onSubmitResume,
//                                              onUploadMedia,
//                                              onRemoveMedia,
//                                          }) {
//     const defaultResume = useMemo(() => {
//         return {
//             resumeId: initialResume?.resumeId || resumeId || "",
//             applicantId: initialResume?.applicantId || applicantId || "",
//
//             headline: initialResume?.headline || "",
//             objective: initialResume?.objective || "",
//
//             minSalary: initialResume?.minSalary ?? "",
//             maxSalary: initialResume?.maxSalary ?? "",
//
//             skills: initialResume?.skills || [],
//             certifications: initialResume?.certifications || [],
//
//             education:
//                 initialResume?.education?.length > 0
//                     ? initialResume.education
//                     : [
//                         {
//                             educationId: "",
//                             applicantId: applicantId || "",
//                             institution: "",
//                             degree: "",
//                             fromYear: "",
//                             toYear: "",
//                             gpa: "",
//                         },
//                     ],
//
//             experience:
//                 initialResume?.experience?.length > 0
//                     ? initialResume.experience
//                     : [
//                         {
//                             workExpId: "",
//                             applicantId: applicantId || "",
//                             jobTitle: "",
//                             companyName: "",
//                             fromYear: "",
//                             toYear: "",
//                             description: "",
//                         },
//                     ],
//
//             mediaPortfolios: initialResume?.mediaPortfolios || [],
//
//             updatedAt: initialResume?.updatedAt || null,
//         };
//     }, [initialResume, applicantId, resumeId]);
//
//     const [formData, setFormData] = useState(defaultResume);
//
//     const [loading, setLoading] = useState(false);
//     const [error, setError] = useState("");
//     const [success, setSuccess] = useState(false);
//
//     // Tag inputs
//     const [skillInput, setSkillInput] = useState("");
//     const [certInput, setCertInput] = useState("");
//
//     // Media upload form state
//     const [showUploadForm, setShowUploadForm] = useState(false);
//     const [selectedFile, setSelectedFile] = useState(null);
//     const [mediaTitle, setMediaTitle] = useState("");
//     const [mediaDescription, setMediaDescription] = useState("");
//     const [visibility, setVisibility] = useState("PUBLIC");
//     const [uploading, setUploading] = useState(false);
//
//     // -----------------------------
//     // Helpers
//     // -----------------------------
//     const updateField = (name, value) => {
//         setFormData((prev) => ({ ...prev, [name]: value }));
//     };
//
//     const updateArrayItem = (arrayName, index, field, value) => {
//         setFormData((prev) => {
//             const cloned = [...prev[arrayName]];
//             cloned[index] = { ...cloned[index], [field]: value };
//             return { ...prev, [arrayName]: cloned };
//         });
//     };
//
//     const addArrayItem = (arrayName, item) => {
//         setFormData((prev) => ({
//             ...prev,
//             [arrayName]: [...prev[arrayName], item],
//         }));
//     };
//
//     const removeArrayItem = (arrayName, index) => {
//         setFormData((prev) => ({
//             ...prev,
//             [arrayName]: prev[arrayName].filter((_, i) => i !== index),
//         }));
//     };
//
//     const addTag = (key, value) => {
//         const cleaned = value.trim();
//         if (!cleaned) return;
//
//         setFormData((prev) => {
//             const current = prev[key] || [];
//             if (current.map((x) => x.toLowerCase()).includes(cleaned.toLowerCase())) {
//                 return prev;
//             }
//             return { ...prev, [key]: [...current, cleaned] };
//         });
//     };
//
//     const removeTag = (key, idx) => {
//         setFormData((prev) => ({
//             ...prev,
//             [key]: prev[key].filter((_, i) => i !== idx),
//         }));
//     };
//
//     const normalizePayload = () => {
//         const minSalary =
//             formData.minSalary === "" ? null : String(formData.minSalary);
//         const maxSalary =
//             formData.maxSalary === "" ? null : String(formData.maxSalary);
//
//         return {
//             ...formData,
//             updatedAt: new Date().toISOString(),
//             minSalary,
//             maxSalary,
//
//             // Ensure each embedded object has applicantId
//             education: (formData.education || [])
//                 .map((e) => ({
//                     ...e,
//                     applicantId: formData.applicantId,
//                     fromYear:
//                         e.fromYear === "" || e.fromYear === null ? null : Number(e.fromYear),
//                     toYear: e.toYear === "" || e.toYear === null ? null : Number(e.toYear),
//                     gpa: e.gpa === "" || e.gpa === null ? null : Number(e.gpa),
//                 }))
//                 .filter(
//                     (e) =>
//                         e.institution?.trim() ||
//                         e.degree?.trim() ||
//                         e.fromYear !== null ||
//                         e.toYear !== null ||
//                         e.gpa !== null
//                 ),
//
//             experience: (formData.experience || [])
//                 .map((x) => ({
//                     ...x,
//                     applicantId: formData.applicantId,
//                     toYear: x.toYear?.trim() ? x.toYear.trim() : null,
//                 }))
//                 .filter(
//                     (x) =>
//                         x.jobTitle?.trim() ||
//                         x.companyName?.trim() ||
//                         x.fromYear?.trim() ||
//                         x.toYear?.trim() ||
//                         x.description?.trim()
//                 ),
//
//             mediaPortfolios: formData.mediaPortfolios || [],
//         };
//     };
//
//     // -----------------------------
//     // Submit resume
//     // -----------------------------
//     const handleSubmit = async (e) => {
//         e.preventDefault();
//         setError("");
//         setSuccess(false);
//
//         if (!formData.applicantId) {
//             setError("Applicant ID is missing.");
//             return;
//         }
//         if (!formData.headline.trim()) {
//             setError("Headline is required.");
//             return;
//         }
//
//         // Salary validation
//         const min = formData.minSalary === "" ? null : Number(formData.minSalary);
//         const max = formData.maxSalary === "" ? null : Number(formData.maxSalary);
//
//         if (min !== null && Number.isNaN(min)) return setError("Min salary must be a number.");
//         if (max !== null && Number.isNaN(max)) return setError("Max salary must be a number.");
//         if (min !== null && max !== null && min > max)
//             return setError("Min salary cannot be greater than max salary.");
//
//         // Education validation (gpa range, year fields)
//         for (const edu of formData.education || []) {
//             if (edu.gpa !== "" && edu.gpa !== null) {
//                 const gpa = Number(edu.gpa);
//                 if (Number.isNaN(gpa) || gpa < 0 || gpa > 100) {
//                     return setError("GPA must be a number between 0 and 100.");
//                 }
//             }
//             if (edu.fromYear !== "" && edu.fromYear !== null) {
//                 const fy = Number(edu.fromYear);
//                 if (Number.isNaN(fy)) return setError("Education From Year must be a number.");
//             }
//             if (edu.toYear !== "" && edu.toYear !== null) {
//                 const ty = Number(edu.toYear);
//                 if (Number.isNaN(ty)) return setError("Education To Year must be a number.");
//             }
//         }
//
//         const payload = normalizePayload();
//
//         try {
//             setLoading(true);
//             await onSubmitResume?.(payload);
//             setSuccess(true);
//         } catch (err) {
//             setError(err?.message || "Failed to update resume.");
//         } finally {
//             setLoading(false);
//         }
//     };
//
//     // -----------------------------
//     // Media upload handlers
//     // -----------------------------
//     const handleMediaFileChange = (e) => {
//         const file = e.target.files?.[0];
//         setSelectedFile(file || null);
//     };
//
//     const handleUpload = async () => {
//         if (!selectedFile) return;
//
//         setError("");
//         try {
//             setUploading(true);
//             const newMedia = await onUploadMedia?.({
//                 file: selectedFile,
//                 title: mediaTitle,
//                 description: mediaDescription,
//                 visibility,
//             });
//
//             // If your API returns the created media object, push it into list
//             if (newMedia) {
//                 setFormData((prev) => ({
//                     ...prev,
//                     mediaPortfolios: [...(prev.mediaPortfolios || []), newMedia],
//                 }));
//             }
//
//             // reset form
//             setSelectedFile(null);
//             setMediaTitle("");
//             setMediaDescription("");
//             setVisibility("PUBLIC");
//             setShowUploadForm(false);
//         } catch (err) {
//             setError(err?.message || "Upload failed.");
//         } finally {
//             setUploading(false);
//         }
//     };
//
//     const handleCancel = () => {
//         setSelectedFile(null);
//         setMediaTitle("");
//         setMediaDescription("");
//         setVisibility("PUBLIC");
//         setShowUploadForm(false);
//     };
//
//     const removeMedia = async (idx) => {
//         const item = formData.mediaPortfolios?.[idx];
//         if (!item) return;
//
//         try {
//             // optional call
//             await onRemoveMedia?.(item.mediaId || item.url || item.id);
//             setFormData((prev) => ({
//                 ...prev,
//                 mediaPortfolios: prev.mediaPortfolios.filter((_, i) => i !== idx),
//             }));
//         } catch (err) {
//             setError(err?.message || "Failed to remove media.");
//         }
//     };
//
//     // -----------------------------
//     // UI
//     // -----------------------------
//     return (
//         <div className="fixed inset-0 bg-black/50 flex items-start justify-center p-4 z-50 overflow-y-auto scrollbar-hide">
//             <div className="bg-white border-4 border-black p-10 w-full max-w-3xl shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] my-8">
//                 {/* Header */}
//                 <div className="flex justify-between items-start mb-6 gap-6">
//                     <div>
//                         <h1 className="text-4xl font-black text-black uppercase">
//                             Update Resume
//                         </h1>
//                         <p className="text-gray-600 font-semibold mt-2">
//                             Fill in your profile and resume details. This will be shown to
//                             employers.
//                         </p>
//                     </div>
//
//                     <button
//                         onClick={onClose}
//                         className="text-black hover:text-primary font-black text-2xl leading-none"
//                         type="button"
//                         disabled={loading || uploading}
//                         aria-label="Close"
//                     >
//                         ✕
//                     </button>
//                 </div>
//
//                 {error && (
//                     <div className="mb-4 p-4 border-4 border-primary bg-primary/10 text-black font-bold">
//                         {error}
//                     </div>
//                 )}
//
//                 {success && (
//                     <div className="mb-4 p-4 border-4 border-green-600 bg-green-100 text-black font-bold">
//                         ✅ Resume updated successfully!
//                     </div>
//                 )}
//
//                 <form onSubmit={handleSubmit} className="space-y-8">
//                     {/* Hidden IDs */}
//                     <input type="hidden" value={formData.resumeId} readOnly />
//                     <input type="hidden" value={formData.applicantId} readOnly />
//
//                     {/* BASIC INFO */}
//                     <section className="space-y-4">
//                         <h2 className="text-xl font-black uppercase text-gray-700 border-b-4 border-black pb-2">
//                             Basic Info
//                         </h2>
//
//                         <input
//                             type="text"
//                             placeholder="HEADLINE (E.G. SOFTWARE ENGINEER)"
//                             value={formData.headline}
//                             onChange={(e) => updateField("headline", e.target.value)}
//                             className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
//                             required
//                             disabled={loading}
//                         />
//
//                         <textarea
//                             placeholder="OBJECTIVE / SUMMARY..."
//                             value={formData.objective}
//                             onChange={(e) => updateField("objective", e.target.value)}
//                             rows={4}
//                             className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark resize-none"
//                             disabled={loading}
//                         />
//                     </section>
//
//                     {/* SALARY */}
//                     <section className="space-y-4">
//                         <h2 className="text-xl font-black uppercase text-gray-700 border-b-4 border-black pb-2">
//                             Salary Expectations
//                         </h2>
//
//                         <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
//                             <input
//                                 type="number"
//                                 min="0"
//                                 step="1"
//                                 placeholder="MIN SALARY"
//                                 value={formData.minSalary}
//                                 onChange={(e) => updateField("minSalary", e.target.value)}
//                                 className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
//                                 disabled={loading}
//                             />
//                             <input
//                                 type="number"
//                                 min="0"
//                                 step="1"
//                                 placeholder="MAX SALARY"
//                                 value={formData.maxSalary}
//                                 onChange={(e) => updateField("maxSalary", e.target.value)}
//                                 className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
//                                 disabled={loading}
//                             />
//                         </div>
//                     </section>
//
//                     {/* SKILLS */}
//                     <section className="space-y-4">
//                         <h2 className="text-xl font-black uppercase text-gray-700 border-b-4 border-black pb-2">
//                             Skills
//                         </h2>
//
//                         <div className="flex gap-2">
//                             <input
//                                 type="text"
//                                 placeholder="ADD A SKILL (PRESS ENTER)"
//                                 value={skillInput}
//                                 onChange={(e) => setSkillInput(e.target.value)}
//                                 onKeyDown={(e) => {
//                                     if (e.key === "Enter") {
//                                         e.preventDefault();
//                                         addTag("skills", skillInput);
//                                         setSkillInput("");
//                                     }
//                                 }}
//                                 className="flex-1 px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
//                                 disabled={loading}
//                             />
//                             <button
//                                 type="button"
//                                 onClick={() => {
//                                     addTag("skills", skillInput);
//                                     setSkillInput("");
//                                 }}
//                                 disabled={loading}
//                                 className="bg-white text-black font-bold py-3 px-6 border-4 border-black uppercase hover:bg-black hover:text-white transition-colors"
//                             >
//                                 Add
//                             </button>
//                         </div>
//
//                         <div className="flex flex-wrap gap-2">
//                             {formData.skills?.map((s, idx) => (
//                                 <span
//                                     key={`${s}-${idx}`}
//                                     className="inline-flex items-center gap-2 border-4 border-black px-3 py-1 font-black uppercase"
//                                 >
//                   {s}
//                                     <button
//                                         type="button"
//                                         onClick={() => removeTag("skills", idx)}
//                                         disabled={loading}
//                                         className="w-6 h-6 border-2 border-black font-black hover:bg-black hover:text-white"
//                                     >
//                     ✕
//                   </button>
//                 </span>
//                             ))}
//                             {(!formData.skills || formData.skills.length === 0) && (
//                                 <p className="text-sm text-gray-500 font-semibold uppercase">
//                                     No skills added yet.
//                                 </p>
//                             )}
//                         </div>
//                     </section>
//
//                     {/* CERTIFICATIONS */}
//                     <section className="space-y-4">
//                         <h2 className="text-xl font-black uppercase text-gray-700 border-b-4 border-black pb-2">
//                             Certifications
//                         </h2>
//
//                         <div className="flex gap-2">
//                             <input
//                                 type="text"
//                                 placeholder="ADD A CERTIFICATION (PRESS ENTER)"
//                                 value={certInput}
//                                 onChange={(e) => setCertInput(e.target.value)}
//                                 onKeyDown={(e) => {
//                                     if (e.key === "Enter") {
//                                         e.preventDefault();
//                                         addTag("certifications", certInput);
//                                         setCertInput("");
//                                     }
//                                 }}
//                                 className="flex-1 px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
//                                 disabled={loading}
//                             />
//                             <button
//                                 type="button"
//                                 onClick={() => {
//                                     addTag("certifications", certInput);
//                                     setCertInput("");
//                                 }}
//                                 disabled={loading}
//                                 className="bg-white text-black font-bold py-3 px-6 border-4 border-black uppercase hover:bg-black hover:text-white transition-colors"
//                             >
//                                 Add
//                             </button>
//                         </div>
//
//                         <div className="flex flex-wrap gap-2">
//                             {formData.certifications?.map((c, idx) => (
//                                 <span
//                                     key={`${c}-${idx}`}
//                                     className="inline-flex items-center gap-2 border-4 border-black px-3 py-1 font-black uppercase"
//                                 >
//                   {c}
//                                     <button
//                                         type="button"
//                                         onClick={() => removeTag("certifications", idx)}
//                                         disabled={loading}
//                                         className="w-6 h-6 border-2 border-black font-black hover:bg-black hover:text-white"
//                                     >
//                     ✕
//                   </button>
//                 </span>
//                             ))}
//                             {(!formData.certifications ||
//                                 formData.certifications.length === 0) && (
//                                 <p className="text-sm text-gray-500 font-semibold uppercase">
//                                     No certifications added yet.
//                                 </p>
//                             )}
//                         </div>
//                     </section>
//
//                     {/* EDUCATION */}
//                     <section className="space-y-4">
//                         <div className="flex items-center justify-between gap-4 border-b-4 border-black pb-2">
//                             <h2 className="text-xl font-black uppercase text-gray-700">
//                                 Education
//                             </h2>
//                             <button
//                                 type="button"
//                                 onClick={() =>
//                                     addArrayItem("education", {
//                                         educationId: "",
//                                         applicantId: formData.applicantId,
//                                         institution: "",
//                                         degree: "",
//                                         fromYear: "",
//                                         toYear: "",
//                                         gpa: "",
//                                     })
//                                 }
//                                 disabled={loading}
//                                 className="bg-white text-black font-bold py-2 px-4 border-4 border-black uppercase hover:bg-black hover:text-white transition-colors"
//                             >
//                                 + Add
//                             </button>
//                         </div>
//
//                         <div className="space-y-6">
//                             {formData.education.map((edu, idx) => (
//                                 <div
//                                     key={idx}
//                                     className="border-4 border-black p-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
//                                 >
//                                     <div className="flex justify-between items-center mb-4">
//                                         <p className="font-black uppercase">Education #{idx + 1}</p>
//                                         <button
//                                             type="button"
//                                             onClick={() => removeArrayItem("education", idx)}
//                                             disabled={loading || formData.education.length === 1}
//                                             className="bg-red-500 text-white font-black py-2 px-4 border-4 border-black uppercase hover:bg-red-600 disabled:opacity-50"
//                                         >
//                                             Remove
//                                         </button>
//                                     </div>
//
//                                     <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
//                                         <input
//                                             type="text"
//                                             placeholder="INSTITUTION"
//                                             value={edu.institution || ""}
//                                             onChange={(e) =>
//                                                 updateArrayItem("education", idx, "institution", e.target.value)
//                                             }
//                                             disabled={loading}
//                                             className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                         />
//
//                                         <input
//                                             type="text"
//                                             placeholder="DEGREE"
//                                             value={edu.degree || ""}
//                                             onChange={(e) =>
//                                                 updateArrayItem("education", idx, "degree", e.target.value)
//                                             }
//                                             disabled={loading}
//                                             className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                         />
//
//                                         <input
//                                             type="number"
//                                             placeholder="FROM YEAR (E.G. 2021)"
//                                             value={edu.fromYear ?? ""}
//                                             onChange={(e) =>
//                                                 updateArrayItem("education", idx, "fromYear", e.target.value)
//                                             }
//                                             disabled={loading}
//                                             className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                         />
//
//                                         <input
//                                             type="number"
//                                             placeholder="TO YEAR (E.G. 2025)"
//                                             value={edu.toYear ?? ""}
//                                             onChange={(e) =>
//                                                 updateArrayItem("education", idx, "toYear", e.target.value)
//                                             }
//                                             disabled={loading}
//                                             className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                         />
//
//                                         <input
//                                             type="number"
//                                             min="0"
//                                             max="100"
//                                             step="0.1"
//                                             placeholder="GPA (0-100)"
//                                             value={edu.gpa ?? ""}
//                                             onChange={(e) =>
//                                                 updateArrayItem("education", idx, "gpa", e.target.value)
//                                             }
//                                             disabled={loading}
//                                             className="md:col-span-2 w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                         />
//                                     </div>
//                                 </div>
//                             ))}
//                         </div>
//                     </section>
//
//                     {/* EXPERIENCE */}
//                     <section className="space-y-4">
//                         <div className="flex items-center justify-between gap-4 border-b-4 border-black pb-2">
//                             <h2 className="text-xl font-black uppercase text-gray-700">
//                                 Work Experience
//                             </h2>
//                             <button
//                                 type="button"
//                                 onClick={() =>
//                                     addArrayItem("experience", {
//                                         workExpId: "",
//                                         applicantId: formData.applicantId,
//                                         jobTitle: "",
//                                         companyName: "",
//                                         fromYear: "",
//                                         toYear: "",
//                                         description: "",
//                                     })
//                                 }
//                                 disabled={loading}
//                                 className="bg-white text-black font-bold py-2 px-4 border-4 border-black uppercase hover:bg-black hover:text-white transition-colors"
//                             >
//                                 + Add
//                             </button>
//                         </div>
//
//                         <div className="space-y-6">
//                             {formData.experience.map((exp, idx) => (
//                                 <div
//                                     key={idx}
//                                     className="border-4 border-black p-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
//                                 >
//                                     <div className="flex justify-between items-center mb-4">
//                                         <p className="font-black uppercase">Experience #{idx + 1}</p>
//                                         <button
//                                             type="button"
//                                             onClick={() => removeArrayItem("experience", idx)}
//                                             disabled={loading || formData.experience.length === 1}
//                                             className="bg-red-500 text-white font-black py-2 px-4 border-4 border-black uppercase hover:bg-red-600 disabled:opacity-50"
//                                         >
//                                             Remove
//                                         </button>
//                                     </div>
//
//                                     <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
//                                         <input
//                                             type="text"
//                                             placeholder="JOB TITLE"
//                                             value={exp.jobTitle || ""}
//                                             onChange={(e) =>
//                                                 updateArrayItem("experience", idx, "jobTitle", e.target.value)
//                                             }
//                                             disabled={loading}
//                                             className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                         />
//                                         <input
//                                             type="text"
//                                             placeholder="COMPANY NAME"
//                                             value={exp.companyName || ""}
//                                             onChange={(e) =>
//                                                 updateArrayItem(
//                                                     "experience",
//                                                     idx,
//                                                     "companyName",
//                                                     e.target.value
//                                                 )
//                                             }
//                                             disabled={loading}
//                                             className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                         />
//
//                                         <input
//                                             type="text"
//                                             placeholder="FROM YEAR (E.G. 2023)"
//                                             value={exp.fromYear || ""}
//                                             onChange={(e) =>
//                                                 updateArrayItem("experience", idx, "fromYear", e.target.value)
//                                             }
//                                             disabled={loading}
//                                             className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                         />
//
//                                         <input
//                                             type="text"
//                                             placeholder="TO YEAR (LEAVE EMPTY IF CURRENT)"
//                                             value={exp.toYear || ""}
//                                             onChange={(e) =>
//                                                 updateArrayItem("experience", idx, "toYear", e.target.value)
//                                             }
//                                             disabled={loading}
//                                             className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                         />
//                                     </div>
//
//                                     <textarea
//                                         placeholder="DESCRIPTION"
//                                         value={exp.description || ""}
//                                         onChange={(e) =>
//                                             updateArrayItem("experience", idx, "description", e.target.value)
//                                         }
//                                         disabled={loading}
//                                         rows={3}
//                                         className="mt-4 w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary resize-none"
//                                     />
//                                 </div>
//                             ))}
//                         </div>
//                     </section>
//
//                     {/* MEDIA PORTFOLIOS */}
//                     <section className="space-y-4">
//                         <div className="flex items-center justify-between gap-4 border-b-4 border-black pb-2">
//                             <h2 className="text-xl font-black uppercase text-gray-700">
//                                 Media Portfolios
//                             </h2>
//
//                             <button
//                                 type="button"
//                                 onClick={() => setShowUploadForm(true)}
//                                 disabled={loading || uploading}
//                                 className="bg-white text-black font-bold py-2 px-4 border-4 border-black uppercase hover:bg-black hover:text-white transition-colors"
//                             >
//                                 + Add Media
//                             </button>
//                         </div>
//
//                         {/* Upload Form (your sample, adapted to brutalist wrapper) */}
//                         {showUploadForm && (
//                             <div className="border-4 border-black p-6 bg-gray-50 space-y-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]">
//                                 <h3 className="font-black text-lg uppercase">Add New Media</h3>
//
//                                 <input
//                                     type="file"
//                                     accept="image/*,video/*"
//                                     onChange={handleMediaFileChange}
//                                     className="block w-full text-gray-600 file:mr-4 file:py-2 file:px-4 file:rounded-none file:border-4 file:border-black file:text-sm file:font-black file:bg-white file:text-black hover:file:bg-black hover:file:text-white"
//                                     disabled={uploading}
//                                 />
//
//                                 <input
//                                     type="text"
//                                     value={mediaTitle}
//                                     onChange={(e) => setMediaTitle(e.target.value)}
//                                     placeholder="TITLE"
//                                     className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                     disabled={uploading}
//                                 />
//
//                                 <textarea
//                                     value={mediaDescription}
//                                     onChange={(e) => setMediaDescription(e.target.value)}
//                                     placeholder="DESCRIPTION"
//                                     className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary resize-none h-32"
//                                     disabled={uploading}
//                                 />
//
//                                 <select
//                                     value={visibility}
//                                     onChange={(e) => setVisibility(e.target.value)}
//                                     className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
//                                     disabled={uploading}
//                                 >
//                                     <option value="PUBLIC">PUBLIC</option>
//                                     <option value="PRIVATE">PRIVATE</option>
//                                 </select>
//
//                                 <div className="flex gap-4">
//                                     <button
//                                         type="button"
//                                         onClick={handleUpload}
//                                         disabled={uploading || !selectedFile}
//                                         className="px-6 py-3 bg-primary text-white border-4 border-black uppercase font-black hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] disabled:opacity-50"
//                                     >
//                                         {uploading ? "UPLOADING..." : "UPLOAD MEDIA"}
//                                     </button>
//
//                                     <button
//                                         type="button"
//                                         onClick={handleCancel}
//                                         disabled={uploading}
//                                         className="px-6 py-3 bg-white text-black border-4 border-black uppercase font-black hover:bg-gray-100"
//                                     >
//                                         Cancel
//                                     </button>
//                                 </div>
//                             </div>
//                         )}
//
//                         {/* Media List */}
//                         <div className="space-y-3">
//                             {(!formData.mediaPortfolios || formData.mediaPortfolios.length === 0) && (
//                                 <p className="text-sm text-gray-500 font-semibold uppercase">
//                                     No media uploaded yet.
//                                 </p>
//                             )}
//
//                             {formData.mediaPortfolios?.map((m, idx) => (
//                                 <div
//                                     key={idx}
//                                     className="border-4 border-black p-4 flex items-start justify-between gap-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
//                                 >
//                                     <div className="min-w-0">
//                                         <p className="font-black uppercase truncate">
//                                             {m.title || "Untitled Media"}
//                                         </p>
//                                         <p className="text-sm text-gray-700 font-semibold mt-1">
//                                             {m.description || "No description"}
//                                         </p>
//                                         <p className="text-xs text-gray-500 font-bold uppercase mt-2">
//                                             {m.visibility || "PUBLIC"}
//                                         </p>
//
//                                         {m.url && (
//                                             <a
//                                                 href={m.url}
//                                                 target="_blank"
//                                                 rel="noreferrer"
//                                                 className="inline-block mt-2 text-sm font-black underline uppercase"
//                                             >
//                                                 Open Media
//                                             </a>
//                                         )}
//                                     </div>
//
//                                     <button
//                                         type="button"
//                                         onClick={() => removeMedia(idx)}
//                                         className="bg-red-500 text-white border-4 border-black px-4 py-2 uppercase font-black hover:bg-red-600 shrink-0"
//                                         disabled={loading || uploading}
//                                     >
//                                         Remove
//                                     </button>
//                                 </div>
//                             ))}
//                         </div>
//                     </section>
//
//                     {/* SUBMIT */}
//                     <button
//                         type="submit"
//                         disabled={loading || uploading}
//                         className="w-full bg-primary text-white font-black py-4 border-4 border-black uppercase hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none flex items-center justify-center gap-2 disabled:opacity-50 disabled:cursor-not-allowed"
//                     >
//                         {loading ? "UPDATING RESUME..." : "UPDATE RESUME"}
//                         <svg
//                             className="w-5 h-5"
//                             fill="none"
//                             stroke="currentColor"
//                             strokeWidth="3"
//                             viewBox="0 0 24 24"
//                         >
//                             <path d="M13 7l5 5m0 0l-5 5m5-5H6" />
//                         </svg>
//                     </button>
//
//                     {/* CANCEL */}
//                     <button
//                         type="button"
//                         onClick={onClose}
//                         disabled={loading || uploading}
//                         className="w-full bg-white text-black font-black py-4 border-4 border-black uppercase hover:bg-gray-100 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
//                     >
//                         CANCEL
//                     </button>
//                 </form>
//             </div>
//         </div>
//     );
// }

import React, {useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { useResume } from "../modules/profile/hooks/useResume";

export default function UpdateResumePage({ onClose }) {
    const navigate = useNavigate();
    const { user } = useSelector((state) => state.auth);
    const applicantId = user?.applicantId;

    const {
        resume: fetchedResume,
        loading,
        isSubmitting,
        updateResume,
    } = useResume(applicantId);

    const [error, setError] = useState("");

    // Tag inputs
    const [skillInput, setSkillInput] = useState("");
    const [certInput, setCertInput] = useState("");

    // Media upload
    const [showUploadForm, setShowUploadForm] = useState(false);
    const [selectedFile, setSelectedFile] = useState(null);
    const [mediaTitle, setMediaTitle] = useState("");
    const [mediaDescription, setMediaDescription] = useState("");
    const [visibility, setVisibility] = useState("PUBLIC");
    const [uploading, setUploading] = useState(false); // ← FIXED: properly declared

    // Sync form with fetched resume
    const defaultResume = useMemo(() => ({
        resumeId: fetchedResume?.resumeId || "",

        headline: fetchedResume?.headline || "",
        objective: fetchedResume?.objective || "",

        minSalary: fetchedResume?.minSalary ?? "",
        maxSalary: fetchedResume?.maxSalary ?? "",

        skills: fetchedResume?.skills || [],
        certifications: fetchedResume?.certifications || [],

        education: fetchedResume?.education?.length > 0
            ? fetchedResume.education
            : [{
                institution: "",
                degree: "",
                fromYear: "",
                toYear: "",
                gpa: ""
            }],

        experience: fetchedResume?.experience?.length > 0
            ? fetchedResume.experience
            : [{
                jobTitle: "",
                companyName: "",
                fromYear: "",
                toYear: "",
                description: ""
            }],

        mediaPortfolios: fetchedResume?.mediaPortfolios || [],
        updatedAt: fetchedResume?.updatedAt || null,
    }), [fetchedResume, applicantId]);

    const [formData, setFormData] = useState(defaultResume);

    // Your original helpers (unchanged)
    const updateField = (name, value) => {
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const updateArrayItem = (arrayName, index, fieldName, value) => {
        setFormData(prev => {
            const cloned = [...prev[arrayName]];
            cloned[index] = { ...cloned[index], [fieldName]: value };
            return { ...prev, [arrayName]: cloned };
        });
    };

    const addArrayItem = (arrayName, item) => {
        setFormData(prev => ({
            ...prev,
            [arrayName]: [...(prev[arrayName] || []), item],
        }));
    };

    const removeArrayItem = (arrayName, index) => {
        setFormData(prev => ({
            ...prev,
            [arrayName]: prev[arrayName].filter((_, i) => i !== index),
        }));
    };

    const addTag = (key, value) => {
        const cleaned = value.trim();
        if (!cleaned) return;

        setFormData(prev => {
            const current = prev[key] || [];
            if (current.map(x => x.toLowerCase()).includes(cleaned.toLowerCase())) return prev;
            return { ...prev, [key]: [...current, cleaned] };
        });
    };

    const removeTag = (key, idx) => {
        setFormData(prev => ({
            ...prev,
            [key]: prev[key].filter((_, i) => i !== idx),
        }));
    };

    // Submit with YOUR validation logic
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        // Salary validation (your original)
        const min = formData.minSalary === "" ? null : Number(formData.minSalary);
        const max = formData.maxSalary === "" ? null : Number(formData.maxSalary);
        if (min !== null && Number.isNaN(min)) return setError("Invalid minimum salary.");
        if (max !== null && Number.isNaN(max)) return setError("Invalid maximum salary.");
        if (min !== null && max !== null && min > max) return setError("Minimum salary cannot be greater than maximum salary.");

        // Education validation (your original)
        for (const edu of formData.education || []) {
            if (edu.gpa !== "" && edu.gpa !== null) {
                const gpa = Number(edu.gpa);
                if (Number.isNaN(gpa) || gpa < 0 || gpa > 100) {
                    return setError("GPA must be a number between 0 and 100.");
                }
            }
            if (edu.fromYear !== "" && edu.fromYear !== null) {
                const fy = Number(edu.fromYear);
                if (Number.isNaN(fy)) return setError("Education From Year must be a number.");
            }
            if (edu.toYear !== "" && edu.toYear !== null) {
                const ty = Number(edu.toYear);
                if (Number.isNaN(ty)) return setError("Education To Year must be a number.");
            }
        }

        const payload = {
            ...formData,
            updatedAt: new Date().toISOString(),
            minSalary: formData.minSalary === "" ? null : Number(formData.minSalary),
            maxSalary: formData.maxSalary === "" ? null : Number(formData.maxSalary),

            education: formData.education.map(e => ({
                ...e,
                applicantId,
                fromYear: e.fromYear ? Number(e.fromYear) : null,
                toYear: e.toYear ? Number(e.toYear) : null,
                gpa: e.gpa ? Number(e.gpa) : null,
            })).filter(e => e.institution?.trim() || e.degree?.trim()),

            experience: formData.experience.map(x => ({
                ...x,
                applicantId,
                fromYear: x.fromYear ? Number(x.fromYear) : null,
                toYear: x.toYear ? Number(x.toYear) : null,
            })).filter(x => x.jobTitle?.trim() || x.companyName?.trim()),
        };

        try {
            await updateResume(payload); // ← saves to MongoDB
            alert("Resume updated successfully!");
            navigate("/profile");
        } catch (err) {
            setError(err.message || "Failed to update resume.");
        }
    };

    // Skip
    const handleSkip = () => {
        navigate("/profile");
    };

    // Media handlers (your original)
    const handleMediaFileChange = (e) => {
        setSelectedFile(e.target.files?.[0] || null);
    };

    const handleUpload = async () => {
        if (!selectedFile) return;
        setUploading(true);
        // Your Cloudinary upload logic here...
        setUploading(false);
        setShowUploadForm(false);
    };

    const handleCancel = () => {
        setSelectedFile(null);
        setMediaTitle("");
        setMediaDescription("");
        setVisibility("PUBLIC"); // Reset to default visibility
        setShowUploadForm(false); // Hide the upload form
        setUploading(false); // In case it was stuck in loading state
    };

    const removeMedia = (idx) => {
        const item = formData.mediaPortfolios?.[idx];
        if (!item) return;

        setFormData(prev => ({
            ...prev,
            mediaPortfolios: prev.mediaPortfolios.filter((_, i) => i !== idx),
        }));
    };

    // Loading state
    if (loading) return <div className="text-center py-20 text-2xl font-black">Loading your resume...</div>;

    return (
        <div className="fixed inset-0 bg-black/50 flex items-start justify-center p-4 z-50 overflow-y-auto scrollbar-hide">
            <div className="bg-white border-4 border-black p-10 w-full max-w-4xl shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] my-8">
                {/* Header */}
                <div className="flex justify-between items-start mb-8">
                    <div>
                        <h1 className="text-4xl font-black uppercase text-black">Update Your Resume</h1>
                        <p className="text-gray-600 font-semibold mt-2">
                            Fill in your details — this helps employers discover you faster.
                        </p>
                    </div>
                    <button
                        onClick={onClose || handleSkip}
                        className="text-3xl font-black hover:text-primary"
                    >
                        ✕
                    </button>
                </div>

                {/* Error message */}
                {error && (
                    <div className="mb-6 p-4 border-4 border-red-600 bg-red-50 text-red-800 font-bold">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-10">
                    {/* Your original form sections — unchanged */}
                    {/* BASIC INFO */}
                    <section className="space-y-4">
                        <h2 className="text-xl font-black uppercase text-gray-700 border-b-4 border-black pb-2">
                            Basic Info
                        </h2>

                        <input
                            type="text"
                            placeholder="HEADLINE (E.G. SOFTWARE ENGINEER)"
                            value={formData.headline}
                            onChange={(e) => updateField("headline", e.target.value)}
                            className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
                            required
                        />

                        <textarea
                            placeholder="OBJECTIVE / SUMMARY..."
                            value={formData.objective}
                            onChange={(e) => updateField("objective", e.target.value)}
                            rows={4}
                            className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark resize-none"
                        />
                    </section>

                    {/* SALARY */}
                    <section className="space-y-4">
                        <h2 className="text-xl font-black uppercase text-gray-700 border-b-4 border-black pb-2">
                            Salary Expectations
                        </h2>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <input
                                type="number"
                                min="0"
                                step="1"
                                placeholder="MIN SALARY"
                                value={formData.minSalary}
                                onChange={(e) => updateField("minSalary", e.target.value)}
                                className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
                            />
                            <input
                                type="number"
                                min="0"
                                step="1"
                                placeholder="MAX SALARY"
                                value={formData.maxSalary}
                                onChange={(e) => updateField("maxSalary", e.target.value)}
                                className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
                            />
                        </div>
                    </section>

                    {/* SKILLS */}
                    <section className="space-y-4">
                        <h2 className="text-xl font-black uppercase text-gray-700 border-b-4 border-black pb-2">
                            Skills
                        </h2>

                        <div className="flex gap-2">
                            <input
                                type="text"
                                placeholder="ADD A SKILL (PRESS ENTER)"
                                value={skillInput}
                                onChange={(e) => setSkillInput(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === "Enter") {
                                        e.preventDefault();
                                        addTag("skills", skillInput);
                                        setSkillInput("");
                                    }
                                }}
                                className="flex-1 px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
                            />
                            <button
                                type="button"
                                onClick={() => {
                                    addTag("skills", skillInput);
                                    setSkillInput("");
                                }}
                                className="bg-white text-black font-bold py-3 px-6 border-4 border-black uppercase hover:bg-black hover:text-white transition-colors"
                            >
                                Add
                            </button>
                        </div>

                        <div className="flex flex-wrap gap-2">
                            {formData.skills?.map((s, idx) => (
                                <span
                                    key={`${s}-${idx}`}
                                    className="inline-flex items-center gap-2 border-4 border-black px-3 py-1 font-black uppercase"
                                >
                  {s}
                                    <button
                                        type="button"
                                        onClick={() => removeTag("skills", idx)}
                                        className="w-6 h-6 border-2 border-black font-black hover:bg-black hover:text-white"
                                    >
                    ✕
                  </button>
                </span>
                            ))}
                            {(!formData.skills || formData.skills.length === 0) && (
                                <p className="text-sm text-gray-500 font-semibold uppercase">
                                    No skills added yet.
                                </p>
                            )}
                        </div>
                    </section>


                    {/* CERTIFICATIONS */}
                    <section className="space-y-4">
                        <h2 className="text-xl font-black uppercase text-gray-700 border-b-4 border-black pb-2">
                            Certifications
                        </h2>

                        <div className="flex gap-2">
                            <input
                                type="text"
                                placeholder="ADD A CERTIFICATION (PRESS ENTER)"
                                value={certInput}
                                onChange={(e) => setCertInput(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === "Enter") {
                                        e.preventDefault();
                                        addTag("certifications", certInput);
                                        setCertInput("");
                                    }
                                }}
                                className="flex-1 px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
                                disabled={loading}
                            />
                            <button
                                type="button"
                                onClick={() => {
                                    addTag("certifications", certInput);
                                    setCertInput("");
                                }}
                                disabled={loading}
                                className="bg-white text-black font-bold py-3 px-6 border-4 border-black uppercase hover:bg-black hover:text-white transition-colors"
                            >
                                Add
                            </button>
                        </div>

                        <div className="flex flex-wrap gap-2">
                            {formData.certifications?.map((c, idx) => (
                                <span
                                    key={`${c}-${idx}`}
                                    className="inline-flex items-center gap-2 border-4 border-black px-3 py-1 font-black uppercase"
                                >
                  {c}
                                    <button
                                        type="button"
                                        onClick={() => removeTag("certifications", idx)}
                                        disabled={loading}
                                        className="w-6 h-6 border-2 border-black font-black hover:bg-black hover:text-white"
                                    >
                    ✕
                  </button>
                </span>
                            ))}
                            {(!formData.certifications ||
                                formData.certifications.length === 0) && (
                                <p className="text-sm text-gray-500 font-semibold uppercase">
                                    No certifications added yet.
                                </p>
                            )}
                        </div>
                    </section>





                    {/* EDUCATION */}
                    <section className="space-y-4">
                        <div className="flex items-center justify-between gap-4 border-b-4 border-black pb-2">
                            <h2 className="text-xl font-black uppercase text-gray-700">
                                Education
                            </h2>
                            <button
                                type="button"
                                onClick={() =>
                                    addArrayItem("education", {
                                        educationId: "",
                                        applicantId: formData.applicantId,
                                        institution: "",
                                        degree: "",
                                        fromYear: "",
                                        toYear: "",
                                        gpa: "",
                                    })
                                }
                                disabled={loading}
                                className="bg-white text-black font-bold py-2 px-4 border-4 border-black uppercase hover:bg-black hover:text-white transition-colors"
                            >
                                + Add
                            </button>
                        </div>

                        <div className="space-y-6">
                            {formData.education.map((edu, idx) => (
                                <div
                                    key={idx}
                                    className="border-4 border-black p-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
                                >
                                    <div className="flex justify-between items-center mb-4">
                                        <p className="font-black uppercase">Education #{idx + 1}</p>
                                        <button
                                            type="button"
                                            onClick={() => removeArrayItem("education", idx)}
                                            disabled={loading || formData.education.length === 1}
                                            className="bg-red-500 text-white font-black py-2 px-4 border-4 border-black uppercase hover:bg-red-600 disabled:opacity-50"
                                        >
                                            Remove
                                        </button>
                                    </div>

                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        <input
                                            type="text"
                                            placeholder="INSTITUTION"
                                            value={edu.institution || ""}
                                            onChange={(e) =>
                                                updateArrayItem("education", idx, "institution", e.target.value)
                                            }
                                            disabled={loading}
                                            className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                        />

                                        <input
                                            type="text"
                                            placeholder="DEGREE"
                                            value={edu.degree || ""}
                                            onChange={(e) =>
                                                updateArrayItem("education", idx, "degree", e.target.value)
                                            }
                                            disabled={loading}
                                            className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                        />

                                        <input
                                            type="number"
                                            placeholder="FROM YEAR (E.G. 2021)"
                                            value={edu.fromYear ?? ""}
                                            onChange={(e) =>
                                                updateArrayItem("education", idx, "fromYear", e.target.value)
                                            }
                                            disabled={loading}
                                            className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                        />

                                        <input
                                            type="number"
                                            placeholder="TO YEAR (E.G. 2025)"
                                            value={edu.toYear ?? ""}
                                            onChange={(e) =>
                                                updateArrayItem("education", idx, "toYear", e.target.value)
                                            }
                                            disabled={loading}
                                            className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                        />

                                        <input
                                            type="number"
                                            min="0"
                                            max="100"
                                            step="0.1"
                                            placeholder="GPA (0-100)"
                                            value={edu.gpa ?? ""}
                                            onChange={(e) =>
                                                updateArrayItem("education", idx, "gpa", e.target.value)
                                            }
                                            disabled={loading}
                                            className="md:col-span-2 w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                        />
                                    </div>
                                </div>
                            ))}
                        </div>
                    </section>




                    {/* EXPERIENCE */}
                    <section className="space-y-4">
                        <div className="flex items-center justify-between gap-4 border-b-4 border-black pb-2">
                            <h2 className="text-xl font-black uppercase text-gray-700">
                                Work Experience
                            </h2>
                            <button
                                type="button"
                                onClick={() =>
                                    addArrayItem("experience", {
                                        workExpId: "",
                                        applicantId: formData.applicantId,
                                        jobTitle: "",
                                        companyName: "",
                                        fromYear: "",
                                        toYear: "",
                                        description: "",
                                    })
                                }
                                disabled={loading}
                                className="bg-white text-black font-bold py-2 px-4 border-4 border-black uppercase hover:bg-black hover:text-white transition-colors"
                            >
                                + Add
                            </button>
                        </div>

                        <div className="space-y-6">
                            {formData.experience.map((exp, idx) => (
                                <div
                                    key={idx}
                                    className="border-4 border-black p-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
                                >
                                    <div className="flex justify-between items-center mb-4">
                                        <p className="font-black uppercase">Experience #{idx + 1}</p>
                                        <button
                                            type="button"
                                            onClick={() => removeArrayItem("experience", idx)}
                                            disabled={loading || formData.experience.length === 1}
                                            className="bg-red-500 text-white font-black py-2 px-4 border-4 border-black uppercase hover:bg-red-600 disabled:opacity-50"
                                        >
                                            Remove
                                        </button>
                                    </div>

                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        <input
                                            type="text"
                                            placeholder="JOB TITLE"
                                            value={exp.jobTitle || ""}
                                            onChange={(e) =>
                                                updateArrayItem("experience", idx, "jobTitle", e.target.value)
                                            }
                                            disabled={loading}
                                            className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                        />
                                        <input
                                            type="text"
                                            placeholder="COMPANY NAME"
                                            value={exp.companyName || ""}
                                            onChange={(e) =>
                                                updateArrayItem(
                                                    "experience",
                                                    idx,
                                                    "companyName",
                                                    e.target.value
                                                )
                                            }
                                            disabled={loading}
                                            className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                        />

                                        <input
                                            type="text"
                                            placeholder="FROM YEAR (E.G. 2023)"
                                            value={exp.fromYear || ""}
                                            onChange={(e) =>
                                                updateArrayItem("experience", idx, "fromYear", e.target.value)
                                            }
                                            disabled={loading}
                                            className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                        />

                                        <input
                                            type="text"
                                            placeholder="TO YEAR (LEAVE EMPTY IF CURRENT)"
                                            value={exp.toYear || ""}
                                            onChange={(e) =>
                                                updateArrayItem("experience", idx, "toYear", e.target.value)
                                            }
                                            disabled={loading}
                                            className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                        />
                                    </div>

                                    <textarea
                                        placeholder="DESCRIPTION"
                                        value={exp.description || ""}
                                        onChange={(e) =>
                                            updateArrayItem("experience", idx, "description", e.target.value)
                                        }
                                        disabled={loading}
                                        rows={3}
                                        className="mt-4 w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary resize-none"
                                    />
                                </div>
                            ))}
                        </div>
                    </section>




                    {/* MEDIA PORTFOLIOS */}
                    <section className="space-y-4">
                        <div className="flex items-center justify-between gap-4 border-b-4 border-black pb-2">
                            <h2 className="text-xl font-black uppercase text-gray-700">
                                Media Portfolios
                            </h2>

                            <button
                                type="button"
                                onClick={() => setShowUploadForm(true)}
                                disabled={loading || uploading}
                                className="bg-white text-black font-bold py-2 px-4 border-4 border-black uppercase hover:bg-black hover:text-white transition-colors"
                            >
                                + Add Media
                            </button>
                        </div>

                        {/* Upload Form (your sample, adapted to brutalist wrapper) */}
                        {showUploadForm && (
                            <div className="border-4 border-black p-6 bg-gray-50 space-y-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]">
                                <h3 className="font-black text-lg uppercase">Add New Media</h3>

                                <input
                                    type="file"
                                    accept="image/*,video/*"
                                    onChange={handleMediaFileChange}
                                    className="block w-full text-gray-600 file:mr-4 file:py-2 file:px-4 file:rounded-none file:border-4 file:border-black file:text-sm file:font-black file:bg-white file:text-black hover:file:bg-black hover:file:text-white"
                                    disabled={uploading}
                                />

                                <input
                                    type="text"
                                    value={mediaTitle}
                                    onChange={(e) => setMediaTitle(e.target.value)}
                                    placeholder="TITLE"
                                    className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                    disabled={uploading}
                                />

                                <textarea
                                    value={mediaDescription}
                                    onChange={(e) => setMediaDescription(e.target.value)}
                                    placeholder="DESCRIPTION"
                                    className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary resize-none h-32"
                                    disabled={uploading}
                                />

                                <select
                                    value={visibility}
                                    onChange={(e) => setVisibility(e.target.value)}
                                    className="w-full px-4 py-3 border-4 border-black font-bold uppercase focus:outline-none focus:ring-4 focus:ring-primary"
                                    disabled={uploading}
                                >
                                    <option value="PUBLIC">PUBLIC</option>
                                    <option value="PRIVATE">PRIVATE</option>
                                </select>

                                <div className="flex gap-4">
                                    <button
                                        type="button"
                                        onClick={handleUpload}
                                        disabled={uploading || !selectedFile}
                                        className="px-6 py-3 bg-primary text-white border-4 border-black uppercase font-black hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] disabled:opacity-50"
                                    >
                                        {uploading ? "UPLOADING..." : "UPLOAD MEDIA"}
                                    </button>

                                    <button
                                        type="button"
                                        onClick={handleCancel}
                                        disabled={uploading}
                                        className="px-6 py-3 bg-white text-black border-4 border-black uppercase font-black hover:bg-gray-100"
                                    >
                                        Cancel
                                    </button>
                                </div>
                            </div>
                        )}

                        {/* Media List */}
                        <div className="space-y-3">
                            {(!formData.mediaPortfolios || formData.mediaPortfolios.length === 0) && (
                                <p className="text-sm text-gray-500 font-semibold uppercase">
                                    No media uploaded yet.
                                </p>
                            )}

                            {formData.mediaPortfolios?.map((m, idx) => (
                                <div
                                    key={idx}
                                    className="border-4 border-black p-4 flex items-start justify-between gap-4 shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]"
                                >
                                    <div className="min-w-0">
                                        <p className="font-black uppercase truncate">
                                            {m.title || "Untitled Media"}
                                        </p>
                                        <p className="text-sm text-gray-700 font-semibold mt-1">
                                            {m.description || "No description"}
                                        </p>
                                        <p className="text-xs text-gray-500 font-bold uppercase mt-2">
                                            {m.visibility || "PUBLIC"}
                                        </p>

                                        {m.url && (
                                            <a
                                                href={m.url}
                                                target="_blank"
                                                rel="noreferrer"
                                                className="inline-block mt-2 text-sm font-black underline uppercase"
                                            >
                                                Open Media
                                            </a>
                                        )}
                                    </div>

                                    <button
                                        type="button"
                                        onClick={() => removeMedia(idx)}
                                        className="bg-red-500 text-white border-4 border-black px-4 py-2 uppercase font-black hover:bg-red-600 shrink-0"
                                        disabled={loading || uploading}
                                    >
                                        Remove
                                    </button>
                                </div>
                            ))}
                        </div>
                    </section>

                    {/* Bottom Buttons */}
                    <div className="flex flex-col sm:flex-row gap-6 mt-12">
                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="flex-1 py-5 bg-primary text-white font-black text-xl border-4 border-black hover:bg-blue-700 disabled:opacity-50"
                        >
                            {isSubmitting ? "SAVING..." : "SAVE RESUME"}
                        </button>

                        <button
                            type="button"
                            onClick={() => navigate("/profile")}
                            disabled={isSubmitting}
                            className="flex-1 py-5 bg-gray-300 text-black font-black text-xl border-4 border-black hover:bg-gray-400 disabled:opacity-50"
                        >
                            SKIP FOR NOW
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}


