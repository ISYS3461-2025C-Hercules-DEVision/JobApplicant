import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { authSuccess, authFail } from "../modules/auth/auth/authSlice.js";

// small helper to decode JWT payload
function decodeJwt(token) {
    try {
        const payload = token.split(".")[1];
        const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split("")
                .map((c) => `%${("00" + c.charCodeAt(0).toString(16)).slice(-2)}`)
                .join("")
        );
        return JSON.parse(jsonPayload);
    } catch {
        return null;
    }
}

export default function AuthCallback() {
    const dispatch = useDispatch();
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const token = params.get("token");

        if (!token) {
            dispatch(authFail("Google login failed: token missing"));
            navigate("/login", { replace: true });
            return;
        }

        // decode token for quick user display (backend puts email + applicantId in claims)
        const decoded = decodeJwt(token);

        const user = decoded
            ? {
                userId: decoded.sub || null,
                email: decoded.email || null,
                applicantId: decoded.applicantId || null,
                // fullName is not in token claims; you'll need /me endpoint for that
                fullName: null,
            }
            : null;

        dispatch(authSuccess({ token, user }));

        // clean URL (remove token from address bar)
        navigate("/", { replace: true });
    }, [dispatch, location.search, navigate]);

    return (
        <div className="min-h-screen flex items-center justify-center bg-light-gray p-4">
            <div className="bg-white border-4 border-black p-8 shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] w-full max-w-md text-center">
                <h1 className="text-2xl font-black uppercase mb-3">Signing you in…</h1>
                <p className="font-bold">
                    Please wait while we finish your Google login.
                </p>
            </div>
        </div>
    );
}
