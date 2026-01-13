import { z } from "zod";
import {
  trimmedString,
  salaryNumberOrNull,
  yearNumberOrNull,
  gpaNumberOrNull_0_100,
  visibilitySchema,
} from "./common/index";

// ---------- Embedded items ----------
export const resumeEducationItemSchema = z
  .object({
    educationId: z.union([z.string(), z.null()]).optional(),
    applicantId: z.string().optional(),
    institution: z.string().optional().default(""),
    degree: z.string().optional().default(""),
    fromYear: yearNumberOrNull(),
    toYear: yearNumberOrNull(),
    gpa: gpaNumberOrNull_0_100(),
  })
  .superRefine((v, ctx) => {
    if (v.fromYear !== null && v.toYear !== null && v.fromYear > v.toYear) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["toYear"],
        message: "To Year must be >= From Year",
      });
    }
  });

export const resumeExperienceItemSchema = z
  .object({
    workExpId: z.union([z.string(), z.null()]).optional(),
    applicantId: z.string().optional(),
    jobTitle: z.string().optional().default(""),
    companyName: z.string().optional().default(""),
    fromYear: z
      .union([z.string(), z.number(), z.null(), z.undefined()])
      .transform((v) => (v == null ? "" : String(v).trim())),
    toYear: z
      .union([z.string(), z.number(), z.null(), z.undefined()])
      .transform((v) => {
        const s = v == null ? "" : String(v).trim();
        return s === "" ? null : s;
      }),
    description: z.string().optional().default(""),
  })
  .superRefine((v, ctx) => {
    // if both numeric-ish, compare
    const fy = v.fromYear ? Number(v.fromYear) : NaN;
    const ty = v.toYear ? Number(v.toYear) : NaN;
    if (v.fromYear && Number.isNaN(fy)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["fromYear"],
        message: "From Year must be a number",
      });
    }
    if (v.toYear !== null && Number.isNaN(ty)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["toYear"],
        message: "To Year must be a number or empty",
      });
    }
    if (!Number.isNaN(fy) && !Number.isNaN(ty) && fy > ty) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["toYear"],
        message: "To Year must be >= From Year",
      });
    }
  });

export const mediaPortfolioSchema = z.object({
  mediaId: z.string().optional(),
  id: z.string().optional(),
  url: z.string().url().optional(),
  fileUrl: z.string().url().optional(),
  title: z.string().optional(),
  description: z.string().optional(),
  visibility: visibilitySchema.optional(),
  mediaType: z.string().optional(), // "VIDEO" | "IMAGE" etc (backend dependent)
});

// ---------- Resume update schema ----------
export const updateResumeSchema = z
  .object({
    resumeId: z.string().optional(),
    applicantId: trimmedString(1, "Applicant ID is missing."),
    headline: trimmedString(1, "Headline is required."),
    objective: z.string().optional().default(""),

    minSalary: salaryNumberOrNull(),
    maxSalary: salaryNumberOrNull(),

    skills: z.array(trimmedString(1)).default([]),
    certifications: z.array(trimmedString(1)).default([]),

    education: z.array(resumeEducationItemSchema).default([]),
    experience: z.array(resumeExperienceItemSchema).default([]),

    mediaPortfolios: z.array(mediaPortfolioSchema).default([]),
    updatedAt: z.string().optional().nullable(),
  })
  .superRefine((data, ctx) => {
    if (data.minSalary !== null && data.maxSalary !== null) {
      if (data.minSalary > data.maxSalary) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: ["maxSalary"],
          message: "Max salary must be >= Min salary",
        });
      }
    }

    // normalize tag duplicates (optional enforcement)
    const lower = (s) => s.trim().toLowerCase();
    const hasDup = (arr) => new Set(arr.map(lower)).size !== arr.length;

    if (hasDup(data.skills)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["skills"],
        message: "Skills contain duplicates",
      });
    }
    if (hasDup(data.certifications)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["certifications"],
        message: "Certifications contain duplicates",
      });
    }
  });

// Helper: validate before submit
export function validateResumePayload(payload) {
  return updateResumeSchema.safeParse(payload);
}
