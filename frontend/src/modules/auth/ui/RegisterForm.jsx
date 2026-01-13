import React, { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useRegister } from "../hooks/useRegister";

export default function RegisterForm() {
    const navigate = useNavigate();

    const {
        formData,
        step,
        loading,
        error,
        isPasswordMismatch,
        handleChange,
        handleSubmit,
        signupWithGoogle,
    } = useRegister({
        onSuccess: () => navigate("/login"),
    });

    // ---------------------------
    // Validation requirements
    // ---------------------------

    // Password: >=8, >=1 digit, >=1 special, >=1 uppercase
    const passwordErrors = useMemo(() => {
        const p = formData.password || "";
        const errs = [];
        if (p.length < 8) errs.push("Password must be at least 8 characters.");
        if (!/[0-9]/.test(p)) errs.push("Password must contain at least 1 number.");
        if (!/[^A-Za-z0-9]/.test(p))
            errs.push("Password must contain at least 1 special character (e.g., $#@!).");
        if (!/[A-Z]/.test(p)) errs.push("Password must contain at least 1 capital letter.");
        return errs;
    }, [formData.password]);

    // Email:
    // - exactly one '@'
    // - at least one '.' after '@'
    // - length < 255
    // - no spaces
    // - no prohibited characters: ( ) [ ] ; :
    const emailErrors = useMemo(() => {
        const e = (formData.email || "").trim();
        const errs = [];

        if (e.length >= 255) errs.push("Email must be less than 255 characters.");
        if (/\s/.test(e)) errs.push("Email must not contain spaces.");
        if (/[()\[\];:]/.test(e))
            errs.push("Email contains prohibited characters: ( ) [ ] ; :");

        const atCount = (e.match(/@/g) || []).length;
        if (atCount !== 1) errs.push("Email must contain exactly one '@' symbol.");

        const atIndex = e.indexOf("@");
        if (atIndex > -1) {
            const domainPart = e.slice(atIndex + 1);
            if (!domainPart.includes(".")) {
                errs.push("Email must contain at least one '.' (dot) after the '@' symbol.");
            } else {
                // ensure dot isn't first/last in domain
                if (domainPart.startsWith(".") || domainPart.endsWith(".")) {
                    errs.push("Email domain format is invalid.");
                }
            }
            // basic sanity: local part and domain part must not be empty
            if (atIndex === 0 || atIndex === e.length - 1) {
                errs.push("Email format is invalid.");
            }
        }

        return errs;
    }, [formData.email]);

    // Phone (if provided):
    // - only digits and must start with valid international dial code (e.g. +84, +49)
    // - digits after dial code length must be less than some limit
    //
    // NOTE: Your requirement text is cut off after "must be less than".
    // Here we use < 15 digits after dial code as a safe common constraint.
    const VALID_DIAL_CODES = useMemo(() => ["+84", "+49", "+1", "+44", "+61", "+65"], []);
    const MAX_DIGITS_AFTER_DIAL_CODE = 15; // adjust to your real requirement

    const phoneErrors = useMemo(() => {
        const raw = (formData.phoneNumber || "").trim();
        const errs = [];

        // If phone is optional, only validate when provided:
        if (!raw) return errs;

        if (!raw.startsWith("+")) {
            errs.push("Phone number must start with '+' followed by an international dial code (e.g., +84).");
            return errs;
        }

        // must be '+' followed by digits only
        if (!/^\+\d+$/.test(raw)) {
            errs.push("Phone number must contain only digits after '+'.");
            return errs;
        }

        const matchedDial = VALID_DIAL_CODES.find((c) => raw.startsWith(c));
        if (!matchedDial) {
            errs.push(`Phone number must start with a valid dial code (e.g., ${VALID_DIAL_CODES.join(", ")}).`);
            return errs;
        }

        const digitsAfter = raw.slice(matchedDial.length);
        if (digitsAfter.length === 0) {
            errs.push("Phone number must include digits after the dial code.");
            return errs;
        }

        if (digitsAfter.length >= MAX_DIGITS_AFTER_DIAL_CODE) {
            errs.push(`Digits after dial code must be less than ${MAX_DIGITS_AFTER_DIAL_CODE}.`);
        }

        return errs;
    }, [formData.phoneNumber, VALID_DIAL_CODES]);

    const allValidationErrors = useMemo(() => {
        return [...emailErrors, ...passwordErrors, ...phoneErrors];
    }, [emailErrors, passwordErrors, phoneErrors]);

    const isFormInvalid =
        loading ||
        isPasswordMismatch ||
        allValidationErrors.length > 0;

    // Define cities based on country
    const citiesByCountry = {
        VN: [
            { value: "Hanoi", label: "Hanoi" },
            { value: "Ho Chi Minh City", label: "Ho Chi Minh City" },
        ],
        SG: [{ value: "Singapore", label: "Singapore" }],
        AU: [
            { value: "Melbourne", label: "Melbourne" },
            { value: "Sydney", label: "Sydney" },
            { value: "Brisbane", label: "Brisbane" },
            { value: "Perth", label: "Perth" },
        ],
    };

    const availableCities = formData.country ? citiesByCountry[formData.country] || [] : [];

    return (
        <div className="min-h-screen bg-light-gray flex items-center justify-center p-4">
            <div className="bg-white border-4 border-black p-10 w-full max-w-md shadow-[8px_8px_0px_0px_rgba(0,0,0,1)]">
                <h1 className="text-4xl font-black text-center text-black mb-8 uppercase">
                    Get started now!
                </h1>

                {(error || isPasswordMismatch || allValidationErrors.length > 0) && (
                    <div className="border-4 border-black p-3 mb-5 font-bold">
                        {error ? (
                            error
                        ) : isPasswordMismatch ? (
                            "Password confirmation does not match."
                        ) : (
                            <ul className="list-disc pl-5 space-y-1">
                                {allValidationErrors.map((msg, idx) => (
                                    <li key={idx}>{msg}</li>
                                ))}
                            </ul>
                        )}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4" noValidate>
                    {step === 1 && (
                        <>
                            <input
                                type="text"
                                name="fullName"
                                placeholder="FULL NAME"
                                value={formData.fullName}
                                onChange={handleChange}
                                className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold placeholder:text-dark"
                                required
                            />

                            <input
                                type="email"
                                name="email"
                                placeholder="EMAIL"
                                value={formData.email}
                                onChange={handleChange}
                                className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold placeholder:text-dark"
                                required
                                maxLength={254}
                            />

                            <input
                                type="password"
                                name="password"
                                placeholder="PASSWORD"
                                value={formData.password}
                                onChange={handleChange}
                                className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold placeholder:text-dark"
                                required
                                minLength={8}
                            />

                            <input
                                type="password"
                                name="passwordConfirmation"
                                placeholder="CONFIRM PASSWORD"
                                value={formData.passwordConfirmation}
                                onChange={handleChange}
                                className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold placeholder:text-dark"
                                required
                            />

                            <input
                                type="tel"
                                name="phoneNumber"
                                placeholder="PHONE NUMBER (OPTIONAL) e.g. +84901234567"
                                value={formData.phoneNumber}
                                onChange={(e) => {
                                    // Allow '+' at first position, and digits otherwise
                                    let v = e.target.value;
                                    v = v.replace(/[^\d+]/g, "");         // remove everything except digits and +
                                    v = v.replace(/\+/g, (m, offset) => (offset === 0 ? "+" : "")); // only one + at start
                                    handleChange({ target: { name: "phoneNumber", value: v } });
                                }}
                                inputMode="tel"
                                className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
                                // optional: do NOT require if it's optional
                                // required
                            />

                            <select
                                name="country"
                                value={formData.country}
                                onChange={(e) => {
                                    handleChange(e);
                                    handleChange({ target: { name: "city", value: "" } });
                                }}
                                className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase"
                                required
                            >
                                <option value="">SELECT COUNTRY</option>
                                <option value="VN">Vietnam</option>
                                <option value="SG">Singapore</option>
                                <option value="AU">Australia</option>
                            </select>

                            <select
                                name="city"
                                value={formData.city}
                                onChange={handleChange}
                                className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase disabled:opacity-50"
                                required
                                disabled={!formData.country}
                            >
                                <option value="">SELECT CITY</option>
                                {availableCities.map((city) => (
                                    <option key={city.value} value={city.value}>
                                        {city.label}
                                    </option>
                                ))}
                            </select>

                            <input
                                type="text"
                                name="streetAddress"
                                placeholder="STREET ADDRESS"
                                value={formData.streetAddress}
                                onChange={handleChange}
                                className="w-full px-4 py-3 border-4 border-black focus:outline-none focus:ring-4 focus:ring-primary font-bold uppercase placeholder:text-dark"
                                required
                            />
                        </>
                    )}

                    <button
                        type="submit"
                        disabled={isFormInvalid}
                        className="w-full bg-primary text-white font-black py-4 border-4 border-black uppercase hover:translate-x-1 hover:translate-y-1 hover:shadow-none shadow-[4px_4px_0px_0px_rgba(0,0,0,1)] transition-none flex items-center justify-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed"
                        title={
                            allValidationErrors.length > 0
                                ? "Please fix validation errors above."
                                : undefined
                        }
                    >
                        {loading ? "Loading..." : step < 3 ? "Create Profile" : "Complete"}
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth="3" viewBox="0 0 24 24">
                            <path d="M13 7l5 5m0 0l-5 5m5-5H6" />
                        </svg>
                    </button>
                </form>

                <div className="flex justify-center items-center gap-4 mt-8">
                    {[1, 2, 3].map((n) => (
                        <div
                            key={n}
                            className={`w-14 h-14 flex items-center justify-center border-4 border-black font-black text-xl ${
                                step === n ? "bg-primary text-white" : "bg-white text-black"
                            }`}
                        >
                            {n}
                        </div>
                    ))}
                </div>

                <div className="relative my-6">
                    <div className="absolute inset-0 flex items-center">
                        <div className="w-full border-t-4 border-black"></div>
                    </div>
                    <div className="relative flex justify-center text-sm">
                        <span className="px-4 bg-white text-black font-black uppercase">OR</span>
                    </div>
                </div>

                <button
                    type="button"
                    onClick={signupWithGoogle}
                    className="w-full flex items-center justify-center gap-3 border-4 border-black hover:bg-black hover:text-white text-black font-bold py-4 uppercase transition-none"
                >
                    <svg className="w-5 h-5" viewBox="0 0 24 24">
                        <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                        <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                        <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                        <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                    </svg>
                    Sign up with Google
                </button>

                <p className="text-center mt-6 text-black font-bold uppercase text-sm">
                    Already have an account?{" "}
                    <Link
                        to="/login"
                        className="text-primary font-black underline underline-offset-4 decoration-4"
                    >
                        Log in
                    </Link>
                </p>
            </div>
        </div>
    );
}
