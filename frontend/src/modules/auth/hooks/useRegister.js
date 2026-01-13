import { useMemo, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { authService } from "../services/authService.js";
import { authStart, authSuccess, authFail } from "../auth/authSlice.js";

import { registerSchema, zodToFieldErrors } from "../../../schemas/registerSchema";
import { getCountries, getStatesByCountry } from "../../../utils/location";

const initial = {
  fullName: "",
  email: "",
  password: "",
  passwordConfirmation: "",
  phoneNumber: "",
  countryIso: "",
  stateIso: "",   
  streetAddress: "",
};

export function useRegister({ onSuccess } = {}) {
  const dispatch = useDispatch();
  const auth = useSelector((state) => state.auth);

  const [formData, setFormData] = useState(initial);
  const [step, setStep] = useState(1);
  const [fieldErrors, setFieldErrors] = useState({});

  // provide dropdown data
  const countries = useMemo(() => getCountries(), []);
  const states = useMemo(
    () => getStatesByCountry(formData.countryIso),
    [formData.countryIso]
  );

  function handleChange(e) {
    const { name, value } = e.target;

    setFormData((p) => {
      // reset state when changing country
      if (name === "countryIso") return { ...p, countryIso: value, stateIso: "" };
      return { ...p, [name]: value };
    });

    setFieldErrors((prev) => ({ ...prev, [name]: undefined, _form: undefined }));
  }

  const isPasswordMismatch = useMemo(() => {
    return (
      formData.password &&
      formData.passwordConfirmation &&
      formData.password !== formData.passwordConfirmation
    );
  }, [formData.password, formData.passwordConfirmation]);

  async function handleSubmit(e) {
    e.preventDefault();

    // (Optional) keep this UI check, but Zod already checks
    if (isPasswordMismatch) return;

    const parsed = registerSchema.safeParse(formData);
    if (!parsed.success) {
      setFieldErrors(zodToFieldErrors(parsed.error));
      return;
    }

    try {
      dispatch(authStart());

      // convert iso -> readable names for backend payload compatibility
      const countryName =
        countries.find((c) => c.isoCode === parsed.data.countryIso)?.name || "";
      const stateName =
        states.find((s) => s.isoCode === parsed.data.stateIso)?.name || "";

      // keep your backend fields: country + city
      const payload = {
        fullName: parsed.data.fullName,
        email: parsed.data.email,
        password: parsed.data.password,
        phoneNumber: parsed.data.phoneNumber,

        // Backend compatible:
        country: countryName,
        city: stateName, // store state/province name here

        // Optional: also send iso codes if backend accepts
        // countryIso: parsed.data.countryIso,
        // stateIso: parsed.data.stateIso,

        streetAddress: parsed.data.streetAddress,
      };

      const data = await authService.register(payload);

      const token = data.token;
      const user = {
        userId: data.userId,
        applicantId: data.applicantId,
        email: data.email,
        fullName: data.fullName,
      };

      if (!token) throw new Error("Token missing from register response");

      dispatch(authSuccess({ token, user }));
      setStep(3);

      onSuccess?.(data);
    } catch (err) {
      const msg = err?.message || "Register failed";
      dispatch(authFail(msg));
      setFieldErrors((prev) => ({ ...prev, _form: msg }));
    }
  }

  function signupWithGoogle() {
    authService.googleLogin();
  }

  return {
    formData,
    step,
    loading: auth.status === "loading",
    error: auth.error,

    fieldErrors,
    countries,
    states,

    isPasswordMismatch,
    handleChange,
    handleSubmit,
    signupWithGoogle,
    setStep,
    setFormData,
  };
}
