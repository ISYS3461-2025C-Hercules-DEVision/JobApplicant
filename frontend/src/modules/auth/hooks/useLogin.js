import { useState } from "react";
import { authService } from "../services/authService.js";

export function useLogin({ onSuccess } = {}) {
    const [formData, setFormData] = useState({ email: "", password: "" });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    function handleChange(e) {
        const { name, value } = e.target;
        setFormData((p) => ({ ...p, [name]: value }));
    }

    async function handleSubmit(e) {
        e.preventDefault();
        setError("");
        setLoading(true);
        try {
            const res = await authService.login(formData);

            // Common patterns: token in res.token, res.accessToken, etc.
            const token = res?.token || res?.accessToken;
            if (token) localStorage.setItem("access_token", token);

            onSuccess?.(res);
        } catch (err) {
            setError(err?.message || "Login failed");
        } finally {
            setLoading(false);
        }
    }

    function loginWithGoogle() {
        authService.googleLogin();
    }

    return {
        formData,
        loading,
        error,
        handleChange,
        handleSubmit,
        loginWithGoogle,
    };
}