import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function AuthCallback() {
    const navigate = useNavigate();

    useEffect(() => {
        const url = new URL(window.location.href);

        // common patterns:
        const token =
            url.searchParams.get("token") ||
            url.searchParams.get("accessToken") ||
            url.searchParams.get("access_token");

        if (token) localStorage.setItem("access_token", token);

        navigate("/", { replace: true });
    }, [navigate]);

    return (
        <div className="min-h-screen flex items-center justify-center font-bold">
            Signing you in...
        </div>
    );
}
