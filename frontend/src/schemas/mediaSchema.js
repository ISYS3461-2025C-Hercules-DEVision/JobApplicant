import { z } from "zod";
import { trimmedString, visibilitySchema } from "./common/primitives";

const MAX_FILE_MB = 25;
const MAX_FILE_SIZE = MAX_FILE_MB * 1024 * 1024;

export const uploadMediaSchema = z.object({
  file: z
    .any()
    .refine((f) => f instanceof File, "File is required")
    .refine((f) => f.size <= MAX_FILE_SIZE, `File must be <= ${MAX_FILE_MB}MB`)
    .refine(
      (f) =>
        typeof f.type === "string" &&
        (f.type.startsWith("image/") || f.type.startsWith("video/")),
      "Only image/video files are allowed"
    ),
  title: z.string().optional().transform((v) => (v ? v.trim() : "")),
  description: z.string().optional().transform((v) => (v ? v.trim() : "")),
  visibility: visibilitySchema.default("PRIVATE"),
});

// for delete by id/url
export const removeMediaSchema = z.object({
  mediaIdOrUrl: trimmedString(1, "Media id/url required"),
});
