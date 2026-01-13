
import { useMemo, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { authService } from "../services/authService.js";
import { authStart, authSuccess, authFail } from "../auth/authSlice.js";

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
    const dispatch = useDispatch();
    const auth = useSelector((state) => state.auth);

    const [formData, setFormData] = useState(initial);
    const [step, setStep] = useState(1);

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
            dispatch(authStart());

            const payload = {
                fullName: formData.fullName,
                email: formData.email,
                password: formData.password,
                phoneNumber: formData.phoneNumber,
                country: formData.country,
                city: formData.city,
                streetAddress: formData.streetAddress,
            };

            const data = await authService.register(payload);
            console.log("REGISTER data typeof:", typeof data);
            console.log("REGISTER data:", data);
            console.log("REGISTER keys:", data && Object.keys(data));

            const token = data?.accessToken;
            console.log("REGISTER accessToken:", token);

            const user = {
                userId: data.userId,
                applicantId: data.applicantId,
                email: data.email,
                fullName: data.fullName,
            };

            if (!token) {
                throw new Error(`Token missing. Keys: ${data ? Object.keys(data).join(",") : "null"}`);
            }

            dispatch(authSuccess({ token, user }));
            setStep(3);

            onSuccess?.(data);
        } catch (err) {
            dispatch(authFail(err.message));
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
        isPasswordMismatch,
        handleChange,
        handleSubmit,
        signupWithGoogle,
        setStep,
    };
}
