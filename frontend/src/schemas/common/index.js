import { z } from "zod";

export const trimmedString = (min = 1, msg = "Required") =>
  z.string().trim().min(min, msg);

export const optionalTrimmedString = () =>
  z
    .string()
    .optional()
    .transform((v) => (typeof v === "string" ? v.trim() : v))
    .refine((v) => v === undefined || v.length >= 0, "Invalid");

export const yearStringOrEmpty = () =>
  z
    .string()
    .optional()
    .transform((v) => (v == null ? "" : String(v).trim()));

export const yearNumberOrNull = () =>
  z
    .union([z.string(), z.number(), z.null(), z.undefined()])
    .transform((v) => {
      if (v === "" || v == null) return null;
      const n = Number(v);
      return Number.isFinite(n) ? n : NaN;
    })
    .refine((v) => v === null || Number.isFinite(v), "Must be a number")
    .refine((v) => v === null || (v >= 1900 && v <= 2100), "Year out of range");

export const salaryNumberOrNull = () =>
  z
    .union([z.string(), z.number(), z.null(), z.undefined()])
    .transform((v) => {
      if (v === "" || v == null) return null;
      const n = Number(v);
      return Number.isFinite(n) ? n : NaN;
    })
    .refine((v) => v === null || Number.isFinite(v), "Must be a number")
    .refine((v) => v === null || v >= 0, "Must be >= 0");

export const gpaNumberOrNull_0_100 = () =>
  z
    .union([z.string(), z.number(), z.null(), z.undefined()])
    .transform((v) => {
      if (v === "" || v == null) return null;
      const n = Number(v);
      return Number.isFinite(n) ? n : NaN;
    })
    .refine((v) => v === null || Number.isFinite(v), "GPA must be a number")
    .refine((v) => v === null || (v >= 0 && v <= 100), "GPA must be 0–100");

export const emailSchema = z.string().trim().email("Invalid email");
export const phoneSchema = z
  .string()
  .trim()
  .min(1, "Phone number required")
  .transform((v) => v.replace(/[^\d]/g, ""))
  .refine((v) => v.length >= 8 && v.length <= 15, "Phone must be 8–15 digits");

export const visibilitySchema = z.enum(["PUBLIC", "PRIVATE"]);
