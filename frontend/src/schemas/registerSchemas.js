// src/validators/registerValidator.js
import { z } from "zod";
import { getStatesByCountry } from "@/utils/location";

const emailSchema = z.string().trim().email("Invalid email format");

const passwordSchema = z
  .string()
  .min(8, "Password must be at least 8 characters")
  .regex(/[a-z]/, "Password must include a lowercase letter")
  .regex(/[A-Z]/, "Password must include an uppercase letter")
  .regex(/\d/, "Password must include a number");

export const registerSchema = z
  .object({
    fullName: z.string().trim().min(2, "Full name must be at least 2 characters"),
    email: emailSchema,
    password: passwordSchema,
    passwordConfirmation: z.string().min(1, "Please confirm your password"),
    phoneNumber: z
      .string()
      .trim()
      .min(1, "Phone number is required")
      .transform((v) => v.replace(/[^\d]/g, ""))
      .refine((v) => v.length >= 8 && v.length <= 15, "Phone must be 8–15 digits"),

    countryIso: z.string().trim().min(2, "Country is required"), // e.g. "VN"
    stateIso: z.string().trim().min(1, "State/Province is required"), // e.g. "SG"
    streetAddress: z.string().trim().min(5, "Street address is required"),
  })
  .superRefine((data, ctx) => {
    if (data.password !== data.passwordConfirmation) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["passwordConfirmation"],
        message: "Passwords do not match",
      });
    }

    // Validate state belongs to country
    const states = getStatesByCountry(data.countryIso);
    const ok = states.some((s) => s.isoCode === data.stateIso);

    if (!ok) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ["stateIso"],
        message: "Please select a valid state/province for this country.",
      });
    }
  });

export function zodToFieldErrors(error) {
  const out = {};
  for (const issue of error.issues || []) {
    const k = issue.path?.[0];
    if (k && !out[k]) out[k] = issue.message;
  }
  return out;
}
