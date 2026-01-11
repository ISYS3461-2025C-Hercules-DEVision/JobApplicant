import { z } from "zod";
import { emailSchema, phoneSchema, trimmedString, yearNumberOrNull, gpaNumberOrNull_0_100 } from "./common/primitives";

// Skills
export const updateSkillsSchema = z.object({
  skills: z
    .array(trimmedString(1, "Skill cannot be empty"))
    .max(50, "Too many skills")
    .default([]),
});

// Contact
export const updateContactSchema = z.object({
  email: emailSchema,
  phoneNumber: phoneSchema,
});

// Education (profile uses "educations" in your code)
export const profileEducationItemSchema = z
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

export const updateEducationsSchema = z.object({
  educations: z.array(profileEducationItemSchema).default([]),
});

// Experience (profile uses "experiences" in your code)
export const profileExperienceItemSchema = z
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
    const fy = v.fromYear ? Number(v.fromYear) : NaN;
    const ty = v.toYear ? Number(v.toYear) : NaN;

    if (v.fromYear && Number.isNaN(fy)) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, path: ["fromYear"], message: "From Year must be a number" });
    }
    if (v.toYear !== null && Number.isNaN(ty)) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, path: ["toYear"], message: "To Year must be a number or empty" });
    }
    if (!Number.isNaN(fy) && !Number.isNaN(ty) && fy > ty) {
      ctx.addIssue({ code: z.ZodIssueCode.custom, path: ["toYear"], message: "To Year must be >= From Year" });
    }
  });

export const updateExperiencesSchema = z.object({
  experiences: z.array(profileExperienceItemSchema).default([]),
});
