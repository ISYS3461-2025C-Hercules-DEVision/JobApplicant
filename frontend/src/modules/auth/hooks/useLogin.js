import { useState } from "react";
import { authService } from "../services/authService.js";
import { useAuthStore } from "../stores/authStore";


export function useLogin({ onSuccess } = {}) {
    const login = useAuthStore((s) => s.login);
  const loading = useAuthStore((s) => s.loading);
  const error = useAuthStore((s) => s.error);

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
