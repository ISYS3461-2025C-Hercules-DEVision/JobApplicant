import { uploadPdf } from "./FileService.js";
import { createApplication } from "./ApplyService.js";

/**
 * Apply to a job:
 * 1) upload CV to Cloudinary
 * 2) create application with documents
 *
 * @param {Object} job
 * @param {File} file
 * @param {string} applicantId
 */
export async function applyToJob({ job, file, applicantId }) {
    if (!applicantId) throw new Error("Missing applicantId");
    if (!job) throw new Error("Missing job");

    const jobPostId = job.id || job.jobId || job.jobPostId;
    const companyId = job.companyId || job.company?.id || job.employerId;

    if (!jobPostId) throw new Error("Missing jobPostId");
    if (!companyId) throw new Error("Missing companyId");

    // ✅ 1) Upload CV
    const uploadRes = await uploadPdf(file);

    const fileUrl = uploadRes?.url;
    const publicId = uploadRes?.publicId;

    if (!fileUrl || !publicId) {
        throw new Error("Upload succeeded but missing url/publicId");
    }

    // ✅ 2) Create application
    const payload = {
        applicantId,
        jobPostId,
        companyId,
        documents: [
            {
                fileUrl,
                publicId,
                fileType: "PDF",
            },
        ],
    };

    const appRes = await createApplication(payload);

    return {
        upload: uploadRes,
        application: appRes,
    };
}
