// import { useMemo, useState } from "react";
// import { authService } from "../services/authService.js";

// const initial = {
//     fullName: "",
//     email: "",
//     password: "",
//     passwordConfirmation: "",
//     phoneNumber: "",
//     country: "",
//     city: "",
//     streetAddress: "",
// };

// export function useRegister({ onSuccess } = {}) {
//     const [formData, setFormData] = useState(initial);
//     const [step, setStep] = useState(1);
//     const [loading, setLoading] = useState(false);
//     const [error, setError] = useState("");

//     function handleChange(e) {
//         const { name, value } = e.target;
//         setFormData((p) => ({ ...p, [name]: value }));
//     }

//     const isPasswordMismatch = useMemo(() => {
//         return (
//             formData.password &&
//             formData.passwordConfirmation &&
//             formData.password !== formData.passwordConfirmation
//         );
//     }, [formData.password, formData.passwordConfirmation]);

//     async function handleSubmit(e) {
//         e.preventDefault();
//         setError("");


//         if (isPasswordMismatch) {
//             setError("Password confirmation does not match.");
//             return;
//         }

//         setLoading(true);
//         try {
//             // Adapt keys here if backend expects different field names
//             const payload = {
//                 fullName: formData.fullName,
//                 email: formData.email,
//                 password: formData.password,
//                 phoneNumber: formData.phoneNumber,
//                 country: formData.country,
//                 city: formData.city,
//                 streetAddress: formData.streetAddress,
//             };

//             const res = await authService.register(payload);

//             // auto-store token if backend returns it
//             const token = res?.token || res?.accessToken;
//             if (token) localStorage.setItem("access_token", token);


//             setStep(3);
//             onSuccess?.(res);
//         } catch (err) {
//             setError(err?.message || "Register failed");
//         } finally {
//             setLoading(false);
//         }
//     }

//     function signupWithGoogle() {
//         authService.googleLogin();
//     }

//     return {
//         formData,
//         step,
//         loading,
//         error,
//         isPasswordMismatch,
//         handleChange,
//         handleSubmit,
//         signupWithGoogle,
//         setStep,
//     };
// }



import { useMemo, useState } from "react";
import { useAuthStore } from "../stores/authStore";

const initial = {
  fullName: "",
  email: "",
  password: "",
  passwordConfirmation: "",
  phoneNumber: "",
  country: "",
  city: "",
  streetAddress: "",
};

export function useRegister({ onSuccess } = {}) {
  const [formData, setFormData] = useState(initial);
  const [step, setStep] = useState(1);

  // zustand
  const register = useAuthStore((s) => s.register);
  const loading = useAuthStore((s) => s.loading);
  const error = useAuthStore((s) => s.error);

  function handleChange(e) {
    const { name, value } = e.target;
    setFormData((p) => ({ ...p, [name]: value }));
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

    if (isPasswordMismatch) return;

    try {
      const payload = {
        fullName: formData.fullName,
        email: formData.email,
        password: formData.password,
        phoneNumber: formData.phoneNumber,
        country: formData.country,
        city: formData.city,
        streetAddress: formData.streetAddress,
      };

      await register(payload); // zustand does API + state

      setStep(3);
      onSuccess?.();
    } catch (err) {
      
    }
  }

  function signupWithGoogle() {
    
    window.location.href = `${import.meta.env.VITE_API_BASE}/oauth2/authorization/google`;
  }

  return {
    formData,
    step,
    loading,
    error,
    isPasswordMismatch,
    handleChange,
    handleSubmit,
    signupWithGoogle,
    setStep,
  };
}
