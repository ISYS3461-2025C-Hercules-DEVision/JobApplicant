import { useMemo, useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import {
    adminAuthStart,
    adminAuthSuccess,
    adminAuthFail,
} from "../../AdminAuth/adminAuthSlice.js";
import { adminLoginService } from "../../services/AdminLoginService/AdminLoginService.jsx";

export default function useAdminLogin() {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [remember, setRemember] = useState(true);
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [status, setStatus] = useState({ type: "", msg: "" });

    const year = useMemo(() => new Date().getFullYear(), []);

    const validate = () => {
        const e = email.trim();
        const p = password.trim();
        if (!e || !/^\S+@\S+\.\S+$/.test(e)) {
            return "Please enter a valid admin email address.";
        }
        if (p.length < 6) {
            return "Password must be at least 6 characters.";
        }
        return "";
    };

    const onSubmit = async (ev) => {
        ev.preventDefault();
        setStatus({ type: "", msg: "" });

        const err = validate();
        if (err) {
            setStatus({ type: "err", msg: err });
            return;
        }

        setLoading(true);
        dispatch(adminAuthStart());

        try {
            const data = await adminLoginService(email, password, remember);

            dispatch(
                adminAuthSuccess({
                    token: data.token,
                    admin: data.admin || { email },
                })
            );

            setStatus({ type: "ok", msg: "Login Successful. Redirecting..." });

            // NAVIGATE
            navigate("/adminDashboard");

            return data;
        } catch (e) {
            dispatch(adminAuthFail(e.message));
            setStatus({ type: "err", msg: e.message || "Login Fail, try again" });
            throw e;
        } finally {
            setLoading(false);
        }
    };

    return {
        year,
        email,
        setEmail,
        password,
        setPassword,
        remember,
        setRemember,
        showPassword,
        setShowPassword,
        loading,
        status,
        onSubmit,
    };
}
