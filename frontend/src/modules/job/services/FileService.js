import { request } from "../../../utils/HttpUtil.js";

export async function uploadPdf(file) {
    if (!file) throw new Error("No file selected");
    if (file.type !== "application/pdf") throw new Error("Only PDF files are allowed");

    const formData = new FormData();
    formData.append("file", file);

    return request("/api/files/upload-pdf", {
        method: "POST",
        body: formData,
        auth: "user",
    });
}
